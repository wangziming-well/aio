package com.wzm.aio.api;

import com.wzm.aio.util.JacksonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class ApiFilter {

    private static final Log logger = LogFactory.getLog(ApiFilter.class);
    private static final String METHOD = "method";
    private static final String URL = "url";
    private static final String BODY = "body";
    private static final String HEADERS = "headers";
    private static final String ATTRIBUTES = "attributes";
    private static final String COOKIES = "cookies";
    private static final String REQUEST_LOG_PREFIX = "HTTP Request: ";
    private static final String RESPONSE_LOG_PREFIX = "HTTP Response: ";

    private static Map<String, Object> requestMap(ClientRequest request) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(METHOD, request.method().toString());
        map.put(URL, request.url().toString());
        map.put(HEADERS, request.headers().toSingleValueMap());
        map.put(ATTRIBUTES, request.attributes());
        map.put(COOKIES, request.cookies());
        return map;
    }

    public static ExchangeFilterFunction logFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            if (logger.isDebugEnabled()) {
                logger.debug(REQUEST_LOG_PREFIX + JacksonUtils.toJsonString(requestMap(request)));
            }
            return Mono.just(request);
        }).andThen(ExchangeFilterFunction.ofResponseProcessor(response -> {

            // 使用 response.mutate() 来克隆响应，保持原始响应体
            if (logger.isDebugEnabled()) {
                return response.bodyToMono(String.class)
                        .flatMap(body -> {
                            Map<String, Object> responseMap = responseMap(response);
                            responseMap.put(BODY, JacksonUtils.parseToMap(body));
                            logger.debug(RESPONSE_LOG_PREFIX + JacksonUtils.toJsonString(responseMap));
                            // 通过创建新的 ClientResponse 来保持原始响应体
                            return Mono.just(response.mutate().body(body).build());
                        }).onErrorResume(e -> {
                            logger.error("Error processing response: ", e);
                            return Mono.just(response);
                        });
            } else {
                return Mono.just(response);
            }

        }));
    }

    private static final String FROM_REQUEST = "from-request";

    private static Map<String, Object> responseMap(ClientResponse response) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(FROM_REQUEST, response.request().getMethod() + " " + response.request().getURI());
        map.put(HEADERS, response.headers().toString());
        map.put(COOKIES, response.cookies());
        return map;
    }
}
