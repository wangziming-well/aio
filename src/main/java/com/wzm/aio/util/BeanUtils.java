package com.wzm.aio.util;

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

}
