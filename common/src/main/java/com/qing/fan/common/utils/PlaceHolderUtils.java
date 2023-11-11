package com.qing.fan.common.utils;

import org.apache.commons.text.StringSubstitutor;

/**
 * 1、可以参考 {@link org.springframework.util.SystemPropertyUtils}
 *
 * @author QingFan
 * @version 1.0.0
 * @date 2022年12月12日 21:16
 */
public final class PlaceHolderUtils {

	private PlaceHolderUtils() {
	}

	public static void main(String[] args) {
		// 根据系统环境变量
		System.out.println(StringSubstitutor.replaceSystemProperties(
				"You are running with java.version = ${java.version} and os.name = ${os.name}."));
	}

}
