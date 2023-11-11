package com.qing.fan.common.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.Charset;
import java.security.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

/**
 * @author QingFan 2021-05-11
 * @version 1.0.0
 */
public class RSAEncrypt {

    private final Logger log = LoggerFactory.getLogger(RSAEncrypt.class);

    private final JedisPool jedisPool;
    private final String[] keys = new String[]{"RSA_KEY_PAIR_A", "RSA_KEY_PAIR_B"};
    private static final Duration keyExpire = Duration.ofDays(1);
    private static final Duration bufferTime = keyExpire.minusHours(20);
    private static final String keyLock = "RSA_KEY_PAIR_LOCK";
    private final Duration lockExpire = Duration.ofMillis(500);

    RSAEncrypt(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public static Duration getBufferTime() {
        return bufferTime;
    }

    /**
     * 对数据进行解密
     *
     * @param encryptData RSA公钥加密的数据
     * @return 私钥解密后的数据
     */
    String decrypt(byte[] encryptData) {
        return getPrivateKey()
                .stream()
                .map(privateKey -> {
                    try {
                        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                        cipher.init(Cipher.DECRYPT_MODE, privateKey);
                        return cipher.doFinal(encryptData);
                    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                        log.error("Try to decrypt failed , message : {}", e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findAny()
                .map(data -> new String(data, Charset.forName("UTF-8")))
                .orElseThrow(() -> new RuntimeException("Encrypt data invalid"));
    }


    /**
     * 获取所有秘钥
     *
     * @return 获取当前所有的私钥
     */
    private List<PrivateKey> getPrivateKey() {
        return getAndSetIfAbsent(0).stream()
                .map(key -> {
                    try {
                        return RSAUtils.privateKey(key.getPrivateKey());
                    } catch (Throwable ex) {
                        throw new RuntimeException("KeyFactory generate private failed", ex);
                    }
                }).collect(toList());
    }

    /**
     * 只为使用方提供最新的Key
     *
     * @return 获取最新的Key
     */
    Key getKey() {
        return getAndSetIfAbsent(0).stream().findFirst().orElseThrow(() -> new RuntimeException("An exception that should not occur"));
    }

    /**
     * 获取秘钥对，如果不存在就设置
     *
     * @param count 递归次数，防止栈溢出
     * @return 返回密钥对，包含公钥和私钥
     */
    private List<Key> getAndSetIfAbsent(int count) {
        /*防止栈溢出，超过3次未获取到公钥，抛出异常放弃获取*/
        if (count == 3)
            throw new RuntimeException("Unable to get public key");

        List<Key> keyPairs = getKeys();

        /*没有秘钥 或者 只有一个秘钥 And 秘钥即将过期*/
        if (keyPairs.isEmpty() || (keyPairs.size() == 1 && keyPairs.get(0).getExpire() <= bufferTime.toMillis())) {
            /*尝试设置密钥*/
            if (!setPublicKey()) {
                /*未抢到锁等待抢到锁的节点设置完成后重新获取秘钥*/
                await();
            }
            return getAndSetIfAbsent(++count);
        }
        return keyPairs;
    }

    /**
     * 获取所有Keys
     * 倒序排列
     *
     * @return 返回Redis存储的所有密钥对
     */
    private List<Key> getKeys() {
        return Stream.of(keys)
                .parallel()
                .map(key -> {
                    try (Jedis jedis = jedisPool.getResource()) {
                        String value = jedis.get(key);
                        if (StringUtils.isBlank(value)) return null;
                        Long expire = jedis.pttl(key);
                        String[] split = value.split(",");
                        return Key.build().setRedisKey(key)
                                .setPrivateKey(split[0])
                                .setPublicKey(split[1])
                                .setExpire(expire);
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Key::getExpire).reversed())
                .collect(toList());
    }

    private void await() {
        try {
            Thread.sleep(lockExpire.toMillis());
        } catch (InterruptedException e) {
            /*ignore*/
        }
    }

    private boolean setPublicKey() {
        try (Jedis jedis = jedisPool.getResource()) {
            try {
                Optional<Long> lock = of(keyLock)
                        .filter(key -> jedis.setnx(key, "lock") == 1)
                        .map(key -> jedis.pexpire(key, lockExpire.toMillis()));
                if (!lock.isPresent()) return false;
                List<Key> pairs = getKeys();
                SetParams px = SetParams.setParams().px(keyExpire.toMillis());
                if (pairs.isEmpty()) {
                    jedis.set(keys[0], keyGen(), px);
                } else if (pairs.size() == 1 && pairs.get(0).getExpire() <= bufferTime.toMillis()) {
                    Key key = pairs.get(0);
                    log.info("Prepare the second key , First one is about to expire , One key : {}", key.getRedisKey() + "-" + key.getExpire());
                    Stream.of(keys)
                            .filter(_key -> !_key.equals(key.getRedisKey()))
                            .findAny()
                            .map(_key -> jedis.set(_key, keyGen(), px))
                            .orElseThrow(() -> new RuntimeException("An exception that should not occur ! "));
                } else {
                    Key oneKey = pairs.get(0);
                    Key secondKey = pairs.get(1);
                    log.info(
                            "The key update phase is in progress ，One key : {}, Second key : {}",
                            oneKey.getRedisKey() + "-" + oneKey.getExpire(),
                            secondKey.getRedisKey() + "-" + secondKey.getExpire()
                    );
                }
                return true;
            } finally {
                jedis.del(keyLock);
            }
        }
    }

    public String keyGen() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair keyPair = keyGen.generateKeyPair();
            String base64Public = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String base64Private = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            return base64Private + "," + base64Public;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Generate key failed", e);
        }
    }

    public static class Key {
        private String redisKey;
        private String privateKey;
        private String publicKey;
        private Long expire;

        public static Key build() {
            return new Key();
        }

        String getRedisKey() {
            return redisKey;
        }

        Key setRedisKey(String redisKey) {
            this.redisKey = redisKey;
            return this;
        }

        String getPrivateKey() {
            return privateKey;
        }

        Key setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public String getPublicKey() {
            return publicKey;
        }

        Key setPublicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Long getExpire() {
            return expire;
        }

        public Key setExpire(Long expire) {
            this.expire = expire;
            return this;
        }
    }
}
