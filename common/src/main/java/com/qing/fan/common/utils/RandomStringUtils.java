package com.qing.fan.common.utils;


import com.blue.fish.common.constant.DateFormatConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 生成随机数
 *
 * @author bluefish 2019-07-07
 * @version 1.0.0
 */
public class RandomStringUtils {


    private static final char[] NUMBERS;

    private static final char[] LETTERS;

    private static final char[] LETTER_WITH_NUMBER;

    static {
        NUMBERS = ("1234567890").toCharArray();

        LETTERS = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz").toCharArray();

        LETTER_WITH_NUMBER = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz").toCharArray();
    }

    /**
     * 从当前时间生成随机字符串：包含微秒
     *
     * @return
     */
    public static String generateStringWithMillSeconds() {
        return DateTimeUtils.formatNow(DateFormatConstant.YYYY_MM_DD_HH_MM_SS_SSS);
    }

    /**
     * 从当前时间生成随机字符串，不包含微秒
     *
     * @return
     */
    public static String generateStringWithSeconds() {
        return DateTimeUtils.formatNow(DateFormatConstant.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 从0~9中获取随机长度的数字字符串
     *
     * @param len 指定长度
     * @return 随机的数字字符串
     */
    public static String generateRandomNumber(int len) {
        return generateRandomFromSource(NUMBERS, len);
    }

    /**
     * 从26个大小写英文字母获取随机长度的字母字符串
     *
     * @param len 位数
     * @return 随机字符串
     */
    public static String generateRandomString(int len) {
        return generateRandomFromSource(LETTERS, len);
    }

    /**
     * 从26个大小写英文字母获取随机长度的字母字符串
     *
     * @param len 位数
     * @return 随机字符串
     */
    public static String generateRandomStringAndNumber(int len) {
        return generateRandomFromSource(LETTER_WITH_NUMBER, len);
    }

    /**
     * 获取随机字符串
     *
     * @param source 指定的字符源
     * @param len    长度
     * @return 从指定源中生成的字符串
     */
    private static String generateRandomFromSource(char[] source, int len) {
        if (len < 1) {
            return "";
        }
        Random r = new Random();

        char[] buffer = new char[len];
        for (int i = 0; i < len; i++) {
            buffer[i] = source[r.nextInt(source.length)];
        }
        return new String(buffer);
    }

    public static void main(String[] args) {
        Random r = new Random();
        ExecutorService pool = Executors.newFixedThreadPool(20);

        List<String> list = new ArrayList<>(10000);

        for (int i = 0; i < 1000000; i++) {
            pool.execute(() -> {
                try {
                    System.out.println(generateRandomNumber( 6));
                } catch (Exception e) {
                    pool.shutdown();
                }
            });
        }

        pool.shutdown();
    }
}
