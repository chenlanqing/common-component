package com.qing.fan.common.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author QingFan
 * @version 1.0.0
 * @date 2022年01月05日 20:56
 */
public class CollectionsUtils {

    /**
     * @param start 生成范围开始
     * @param end 生成范围结束
     * @param closed 是否包含末尾元素
     * @return 返回集合
     */
    public static List<Integer> generateList(int start, int end, boolean closed) {
        if (closed) {
            return IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
        }
        return IntStream.range(start, end).boxed().collect(Collectors.toList());

    }
}
