package com.wzm.aio.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OpenApiBuilder<T> {

    private static final int DEFAULT_MAX_MEMORY_SIZE = 1024 * 1024 * 2;
    private int maxMemorySize = DEFAULT_MAX_MEMORY_SIZE;
    private static final int DEFAULT_TIME_OUT = 30;
    private int timeout = DEFAULT_TIME_OUT;
    private final String baseUrl;

    private final Class<T> clazz;
    private HttpHeaders defaultHttpHeaders = new HttpHeaders();
    private final List<ExchangeFilterFunction> filters = new ArrayList<>();
    private static final Log logger = LogFactory.getLog(OpenApiBuilder.class);

    private OpenApiBuilder(String baseUrl, Class<T> clazz) {
        this.baseUrl = baseUrl;
        this.clazz = clazz;
    }

    public static <T> OpenApiBuilder<T> builder(String baseUrl, Class<T> clazz) {
        return new OpenApiBuilder<>(baseUrl, clazz);
    }

    public OpenApiBuilder<T> defaultHeaders(HttpHeaders defaultHttpHeaders) {
        this.defaultHttpHeaders = defaultHttpHeaders;
        return this;
    }


    public OpenApiBuilder<T> addFilter(ExchangeFilterFunction filter) {
        filters.add(filter);
        return this;
    }

    public OpenApiBuilder<T> timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public OpenApiBuilder<T> maxMemorySize(int maxMemorySize) {
        this.maxMemorySize = maxMemorySize;
        return this;
    }

    public T build() {
        WebClient webClient = WebClient.builder()
                .baseUrl(this.baseUrl)
                .defaultHeaders(httpHeaders -> httpHeaders.addAll(defaultHttpHeaders))
                .filters(filters -> filters.addAll(this.filters))
                .codecs(configurer
                        -> configurer.defaultCodecs()
                        .maxInMemorySize(this.maxMemorySize))
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(this.timeout))))
                .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(this.clazz);
    }
}
