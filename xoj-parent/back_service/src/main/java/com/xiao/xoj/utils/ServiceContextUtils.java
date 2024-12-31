package com.xiao.xoj.utils;

import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author 肖恩
 * @create 2023/7/8 18:04
 * @description: TODO
 */
public class ServiceContextUtils {
    /**
     * 先从method上获取注解，获取不到再从class上获取
     *
     * @param method
     * @param clazz
     * @param annotationClass
     * @param <T>
     * @return 注解对象
     */
    public static <T extends Annotation> T getAnnotation(Method method, Class<?> clazz, Class<T> annotationClass) {
        T annotation = AnnotationUtils.getAnnotation(method, annotationClass);
        if (annotation == null) {
            annotation = AnnotationUtils.findAnnotation(clazz, annotationClass);
        }
        return annotation;
    }
}
