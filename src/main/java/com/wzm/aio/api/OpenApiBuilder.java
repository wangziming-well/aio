package com.wzm.aio.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;

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
    private final HttpHeaders defaultHttpHeaders = new HttpHeaders();
    private final List<ExchangeFilterFunction> filters = new ArrayList<>();

    private OpenApiBuilder(String baseUrl, Class<T> clazz) {
        this.baseUrl = baseUrl;
        this.clazz = clazz;
    }

    public static <T> OpenApiBuilder<T> builder(String baseUrl, Class<T> clazz) {
        Assert.notNull(baseUrl, "baseUrl不能为空");
        Assert.notNull(clazz, "clazz不能为空");
        return new OpenApiBuilder<>(baseUrl, clazz);
    }

    public OpenApiBuilder<T> defaultHeaders(HttpHeaders defaultHttpHeaders) {
        this.defaultHttpHeaders.addAll(defaultHttpHeaders);
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
        WebClient webClient = getWebClient();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(this.clazz);
    }

    private WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(this.baseUrl)
                .defaultHeaders(httpHeaders -> httpHeaders.addAll(defaultHttpHeaders))
                .filters(filters -> filters.addAll(this.filters))
                .codecs(configurer
                        -> configurer.defaultCodecs()
                        .maxInMemorySize(this.maxMemorySize))
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(this.timeout))))
                .build();
    }
}
