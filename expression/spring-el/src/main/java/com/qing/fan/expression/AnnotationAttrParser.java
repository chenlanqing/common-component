package com.qing.fan.expression;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author QingFan
 * @version 1.0.0
 * @date 2023年11月06日 21:20
 */
@Slf4j
public class AnnotationAttrParser {

    private static final ConcurrentHashMap<Method, Map<String, Object>> cache = new ConcurrentHashMap<>(200);

    public <T extends Annotation> Map<String, Object> parse(Method target, Class<T> clazz) {
        var annotationAttributes = cache.get(target);
        if (annotationAttributes != null && !annotationAttributes.isEmpty()) {
            return annotationAttributes;
        }
        return doParse(target, clazz);
    }

    private <T extends Annotation> Map<String, Object> doParse(Method method, Class<T> clazz) {
        try {
            var annotationAttributes = Optional.<Annotation>ofNullable(AnnotationUtils.findAnnotation(method, clazz))
                    .map(AnnotationUtils::getAnnotationAttributes)
                    .orElse(new HashMap<>(0));
            cache.put(method, annotationAttributes);
            return annotationAttributes;
        } catch (Exception exception) {
            log.warn("An error happened while parsing operation log annotation info.");
        }
        return new HashMap<>(0);
    }
}
