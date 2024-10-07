package com.wzm.aio.api;

import com.wzm.aio.util.JacksonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
// todo 需要重构
public class WebClientLogFilter {

    private static final Log logger = LogFactory.getLog(WebClientLogFilter.class);
    private static final String METHOD = "method";
    private static final String URL = "url";
    private static final String BODY = "body";
    private static final String HEADERS = "headers";
    private static final String ATTRIBUTES = "attributes";
    private static final String COOKIES = "cookies";
    private static final String HTTP_ID = "http-id";
    private static final String RESPONSE_TIME = "response-time";
    private static final String REQUEST_LOG_PREFIX = "HTTP Request: ";
    private static final String RESPONSE_LOG_PREFIX = "HTTP Response: ";
    private static final ConcurrentHashMap<String, Long> requestMap = new ConcurrentHashMap<>();

    public static ExchangeFilterFunction logFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            logRequest(request);
            return Mono.just(request);
        }).andThen(ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (logger.isDebugEnabled()) {
                Map<String, Object> logMap = getResponseLogMap(response);
                return response.bodyToMono(String.class)
                        .flatMap(body -> {
                            if (response.statusCode().is2xxSuccessful() )
                                logMap.put(BODY, JacksonUtils.parseToMap(body));
                            else
                                logMap.put(BODY, body);
                            logger.debug(RESPONSE_LOG_PREFIX + JacksonUtils.toJsonString(logMap));
                            return Mono.just(response.mutate().body(body).build()); // 使用 response.mutate() 来克隆响应，保持原始响应体
                        }).onErrorResume(e -> {
                            logger.error("Error processing response: ", e);
                            return Mono.just(response);
                        });
            } else {
                return Mono.just(response);
            }
        }));
    }

    private static void logRequest(ClientRequest request){
        if (logger.isDebugEnabled()) {
            String httpId = getRequestId(request.logPrefix());
            requestMap.put(httpId, System.currentTimeMillis());
            Map<String, Object> logMap = requestMap(request);
            logMap.put(HTTP_ID, httpId);
            logger.debug(REQUEST_LOG_PREFIX + JacksonUtils.toJsonString(logMap));
        }
    }

    private static Map<String,Object> getResponseLogMap(ClientResponse response){
        String httpId = getResponseId(response.logPrefix());
        Map<String, Object> logMap = responseMap(response);
        logMap.put(HTTP_ID, httpId);
        Long startTime = requestMap.get(httpId);
        if (Objects.nonNull(startTime)) {
            logMap.put(RESPONSE_TIME, (System.currentTimeMillis() - startTime) + "ms");
            requestMap.remove(httpId);
        }
        return logMap;
    }

    private static String extractId(String logPrefix) {
        logPrefix = logPrefix.trim();
        return logPrefix.substring(1, logPrefix.length() - 1);
    }
    // 输入:[5f6584a9] 输出: 5f6584a9

    private static String getRequestId(String requestLogPrefix) {
        return extractId(requestLogPrefix);
    }
    // 输入:[5f6584a9] [20aa0390-1] 输出: 5f6584a9
    private static String getResponseId(String responseLogPrefix) {
        return extractId(responseLogPrefix.split(" ")[0]);
    }


    private static Map<String, Object> requestMap(ClientRequest request) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(METHOD, request.method().toString());
        map.put(URL, request.url().toString());
        map.put(HEADERS, request.headers().toSingleValueMap());
        map.put(ATTRIBUTES, request.attributes());
        map.put(COOKIES, request.cookies());
        return map;
    }

    private static Map<String, Object> responseMap(ClientResponse response) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(HEADERS, response.headers().asHttpHeaders().toSingleValueMap());
        map.put(COOKIES, response.cookies());
        return map;
    }
}
