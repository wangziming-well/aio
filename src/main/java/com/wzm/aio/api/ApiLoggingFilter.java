package com.wzm.aio.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public class ApiLoggingFilter {

    private static final Log logger = LogFactory.getLog(ApiLoggingFilter.class);

    public static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {

            logger.debug("HTTP Request: "+ clientRequest.method() + clientRequest.url());
            clientRequest.headers().forEach((name, values) -> {
                for (String value : values) {
                    logger.debug(name+": "+value);
                }
            });
            return Mono.just(clientRequest);
        });
    }

    public static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            logger.debug("Response status code: " + clientResponse.statusCode());
            return clientResponse.bodyToMono(String.class)
                    .flatMap(body -> {
                        logger.debug("Response body: "+ body);
                        return Mono.just(clientResponse);
                    });
        });
    }

    public static ExchangeFilterFunction dealException() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (!response.statusCode().is2xxSuccessful()) {
                logger.error("请求失败: " + response.statusCode());
                return Mono.error(new RuntimeException("API 请求失败: " + response.statusCode()));
            }
            return Mono.just(response);
        });
    }
}
