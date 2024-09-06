package com.wzm.aio.api;

import com.wzm.aio.service.MomoService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.Charset;
import java.util.Arrays;

@Aspect
@Component
public class MomoOpenApiAspect {

    private static final Log logger = LogFactory.getLog(MomoOpenApiAspect.class);

    @Around("execution(* *..MomoOpenApi.*(..))")
    public ResponseEntity<MomoResponse<?>> advice(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String signature = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        ResponseEntity<MomoResponse<?>>  result;
        try {
            result =(ResponseEntity<MomoResponse<?>> ) joinPoint.proceed();
            long len = System.currentTimeMillis() - start;
            checkResponse(result);
            logger.info("方法["+signature+"]调用成功;入参:"+ Arrays.toString(args) +"耗时:"+ len +" ms;响应结果:"+result);
        } catch (WebClientResponseException e){
            logger.info("方法调用["+signature+"]失败;入参:"+ Arrays.toString(args) +"错误信息:" +e.getMessage());
            throw e;
        }
        return result;
    }

    private void checkResponse(ResponseEntity<MomoResponse<?>> result) {
        if (result.getStatusCode().isError())
            throw new MomoApiResponseException("响应错误，响应码码:" + result.getStatusCode());
        MomoResponse<?> body = result.getBody();
        if (body == null)
            throw new MomoApiResponseException("响应错误，响应体为空");
        if (!body.isSuccess())
            throw new MomoApiResponseException("响应错误，响应体:" + body);
    }

    public static class MomoApiResponseException extends WebClientException {
        public MomoApiResponseException(String msg) {
            super(msg);
        }
    }

}
