package com.qing.fan.common.utils;

import org.hashids.Hashids;

import java.util.HashMap;
import java.util.Map;

/**
 * @author QingFan 2020-08-29
 * @version 1.0.0
 */
public class HashIdUtil {

    private static final char CURRENT_VERSION = '1';

    private static final String HASH_ID_FOR_ZERO = CURRENT_VERSION + "0";

    private static final Map<Character, Hashids> HASH_IDSMap = new HashMap<>();

    private HashIdUtil() {
    }

    static {
        HASH_IDSMap.put(CURRENT_VERSION, new Hashids("salt", 12));
    }

    /**
     * id转化为hash编码。
     * <p>
     * id不允许小于0，同时id为0的不进行 hashIds 运算。
     * 编码的第一位表示当前编码算法的版本号，当日后编解码算法改变后，依然能提供一段时间老版本编码值的识别。
     *
     * @param id 待编码的id
     * @return
     */
    public static String idToHash(long id) {

        if (id < 0) {
            throw new IllegalArgumentException("id less than 0 is not allowed, id: " + id);
        }

        if (id == 0) {
            return HASH_ID_FOR_ZERO;
        }

        return CURRENT_VERSION + HASH_IDSMap.get(CURRENT_VERSION).encode(id);
    }

    /**
     * hash编码转化为id。
     *
     * @param encode 待解码值
     * @return
     */
    public static long hashToId(String encode) {
        if (encode == null || encode.length() <= 1) {
            throw new IllegalArgumentException("hashId decode error, value: " + encode);
        }

        char version = encode.charAt(0);
        Hashids hashids = HASH_IDSMap.get(version);
        if (hashids == null) {
            throw new IllegalArgumentException("hashId decode error, hashId version error, value: " + encode);
        }

        String hashId = encode.substring(1);
        if (hashId.equals("0")) {
            return 0;
        } else {
            long[] decode = hashids.decode(hashId);
            if (decode.length != 1) {
                throw new IllegalArgumentException("hashId decode error, value: " + encode);
            }
            return decode[0];
        }
    }
}
