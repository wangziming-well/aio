package com.wzm.aio.util;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

public class SpringUtils {

    private static volatile ApplicationContext  applicationContext = null;

    public static void setApplicationContext(ApplicationContext applicationContext){
        SpringUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() throws IllegalAccessException {
        if (SpringUtils.applicationContext == null)
            throw new IllegalAccessException("applicationContext还未设置");
        return SpringUtils.applicationContext;
    }



}
