package com.wzm.aio.api;

import com.wzm.aio.util.JacksonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Aspect
@Component
public class OpenApiAspect {

    private static final Log logger = LogFactory.getLog(OpenApiAspect.class);


    @Around("execution(* com.wzm.aio.api..*OpenApi.*(..))")
    public Object advice(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String signature = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        Object result;
        try {
            result = joinPoint.proceed();
            long len = System.currentTimeMillis() - start;
            logger.debug("方法[" + signature + "]调用成功;入参:" + JacksonUtils.toJsonString(args) + "耗时:" + len + " ms;响应结果:" + JacksonUtils.toJsonString(result));
        } catch (WebClientResponseException e) {
            logger.debug("方法调用[" + signature + "]失败;入参:" + JacksonUtils.toJsonString(args) + "错误信息:" + e.getMessage());
            throw e;
        }
        return result;
    }
}
