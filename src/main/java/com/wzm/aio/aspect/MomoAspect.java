package com.wzm.aio.aspect;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MomoAspect {

    private static final Log logger = LogFactory.getLog(MomoAspect.class);

    @Pointcut("execution(public * com.wzm.aio.service.MomoService.*(..))")
    public void allMethod(){}

    @Pointcut("execution(public * com.wzm.aio.service.MomoService.log*(..))")
    public void authMethod(){}

    //对MomoService中所有非login/logout方法
    @Before("allMethod() && !authMethod()")
    public void checkCookies(JoinPoint joinPoint){
/*        if (MomoUserTokenManager.INSTANCE.isEmpty())
            throw new RuntimeException("调用方法" +joinPoint.getSignature()+"前请先登录");*/
    }

}
