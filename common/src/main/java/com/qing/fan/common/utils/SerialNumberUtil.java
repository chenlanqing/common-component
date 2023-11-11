package com.qing.fan.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author User
 **/
public class SerialNumberUtil {

    /**
     * 生产流水
     */
    public static String createSerial(int len, Integer i, String driver) {
        String dr;
        AtomicInteger atomicInteger = new AtomicInteger(i);
        atomicInteger.getAndIncrement();
        if (atomicInteger.toString().length() > (len - (driver != null ? driver.length() : 0))) {
            assert driver != null;
            dr = driverCheck(driver, len);
            //如超出限定长度并字母都为Z的时候，限定长度加1，dr重新开始，默认为空
            if (dr.equals(".N")) {
                len++;
                dr = "";
            } else {
                atomicInteger.set(1);
            }
        } else {
            dr = driver;
        }
        if (dr.length() == len) {
            return dr;
        } else {
            return String.format("%0" + (len - dr.length()) + "d", atomicInteger.intValue()) + dr;
        }
    }

    /**
     * 字母有效检查
     * 1.检查字母是否都为Z
     * 2.检查字母长度
     */
    public static String driverCheck(String driver, int len) {
        char[] charArray = driver.toCharArray();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (char c : charArray) {
            if (c == 'Z') {
                atomicInteger.getAndIncrement();
            }
        }
        //如所有字母都为Z，并且长度达到限定长度，返回.N
        if (atomicInteger.intValue() == driver.length() && atomicInteger.intValue() == len) {
            return ".N";
        } else if (atomicInteger.intValue() == driver.length() && atomicInteger.intValue() < len) {
            //如果所有字母都为Z，但长度未达到限定长度，则在调用字母递增方法之前加入@用以递增A
            return driverV2("@" + driver);
        } else {
            //以上两个条件都不满足，则直接递增
            return driverV2(driver);
        }
    }

    /**
     * 字母递增(带符号的自增)
     */
    public static String driverV1(String driver) {
        if (driver != null && driver.length() > 0) {
            char[] charArray = driver.toCharArray();
            AtomicInteger z = new AtomicInteger(0);
            for (int i = charArray.length - 1; i > -1; i--) {
                if (charArray[i] == 'Z') {
                    z.set(z.incrementAndGet());
                } else {
                    if (z.intValue() > 0 || i == charArray.length - 1) {
                        AtomicInteger atomicInteger = new AtomicInteger(charArray[i]);
                        charArray[i] = (char) atomicInteger.incrementAndGet();
                        z.set(0);
                    }
                }
            }
            return String.valueOf(charArray);
        } else {
            return "A";
        }
    }

    /**
     * 字母递增(不带符号的自增)
     */
    public static String driverV2(String driver) {
        if (StringUtils.isBlank(driver) || driver.length() == 0) {
            return "A";
        }
        char[] charArray = driver.toCharArray();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = charArray.length - 1; i >= 0; i--) {
            // 先判断满Z进1的情况
            if (charArray[i] == 'Z') {
                charArray[i] = 'A';
                atomicInteger.set(1);
                continue;
            }
            if (charArray[i] == '9') {
                // 如果到了第一位还是9
                if (i == 0) {
                    charArray[i] = 'A';
                    break;
                }
                charArray[i] = '0';
                atomicInteger.set(1);
                continue;
            }
            // 如果等于1则需要进位1
            if (atomicInteger.intValue() == 1 || i == charArray.length - 1) {
                //虽然说是不带符号的自增，但是还是防止抬杠的输入符号，所以这里过滤一遍
                String s = String.valueOf(charArray[i]);
                //符号集合 注意一些符号的转义
                String tmp = s.replaceAll("\\p{P}", "");
                if (s.length() != tmp.length()) {
                    charArray[i] = 'A';
                    break;
                }
                AtomicInteger atomic = new AtomicInteger(charArray[i]);
                charArray[i] = (char) atomic.incrementAndGet();
                atomicInteger.set(0);
                break;
            }
        }
        return String.valueOf(charArray);
    }


    public static void main(String[] args) {
        String orgId = "00604891i";
        String orgPid = "0060489";
        //按照父级机构规律的位数生产
        String sn = SerialNumberUtil.createSerial(orgId.length() - orgPid.length(), 0, orgId.substring(orgPid.length()).toUpperCase());
        System.out.println(sn);
    }

}