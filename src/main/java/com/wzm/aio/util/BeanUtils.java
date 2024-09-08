package com.wzm.aio.util;

import io.github.linpeilie.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class BeanUtils {

    public static <T> T transfer(Object obj, Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getConstructor();
            T t = constructor.newInstance();
            org.apache.commons.beanutils.BeanUtils.copyProperties(t, obj);
            return t;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convert(Object obj,Class<T> clazz){
        Converter converter;
        try {
            ApplicationContext applicationContext = SpringUtils.getApplicationContext();
            converter = applicationContext.getBean(Converter.class);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return converter.convert(obj,clazz);
    }

}
