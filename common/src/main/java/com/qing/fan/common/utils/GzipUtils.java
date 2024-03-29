package com.qing.fan.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author QingFan 2020-09-23
 * @version 1.0.0
 */
public class GzipUtils {

    private GzipUtils() {
    }

    /**
     * 将字节数组压缩
     *
     * @param data 源字节数组
     * @return 压缩后的字节数组
     * @throws Exception 处理异常
     */
    public static byte[] gzip(byte[] data) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data);
        gzip.finish();
        gzip.close();
        byte[] ret = bos.toByteArray();
        bos.close();
        return ret;
    }

    /**
     * 字节数组解压缩
     *
     * @param data 源字节数组
     * @return 解压缩后的字节数组
     * @throws Exception 处理异常
     */
    public static byte[] ungzip(byte[] data) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        GZIPInputStream gzip = new GZIPInputStream(bis);
        byte[] buf = new byte[1024];
        int num = -1;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((num = gzip.read(buf, 0, buf.length)) != -1) {
            bos.write(buf, 0, num);
        }
        gzip.close();
        bis.close();
        byte[] ret = bos.toByteArray();
        bos.flush();
        bos.close();
        return ret;
    }
}
