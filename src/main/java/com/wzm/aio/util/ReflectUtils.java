package com.wzm.aio.util;

import org.springframework.aop.framework.Advised;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class ReflectUtils {

    public static Object getField(Object obj ,String fieldName){
        try {
            if (obj instanceof Advised advised)
                obj = advised.getTargetSource().getTarget();
            Assert.notNull(obj,"obj不能为空");
            Field declaredField = obj.getClass().getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            return declaredField.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}