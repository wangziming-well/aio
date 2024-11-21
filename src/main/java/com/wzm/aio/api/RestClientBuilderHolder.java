package com.wzm.aio.api;


import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * 持有RestClient.Builder实例和与之关联的HttpComponentsClientHttpRequestFactory
 * 如果两者没有关联，存入该Holder中也会自动建立关联
 */
public class RestClientBuilderHolder {

    private final RestClient.Builder builder;

    private final HttpComponentsClientHttpRequestFactory associatedHttpRequestFactory;

    public RestClientBuilderHolder(RestClient.Builder builder, HttpComponentsClientHttpRequestFactory associatedHttpRequestFactory) {
        this.builder = builder;
        this.associatedHttpRequestFactory = associatedHttpRequestFactory;
        this.builder.requestFactory(this.associatedHttpRequestFactory);
    }

    public RestClient.Builder get(){
        return this.builder;
    }

    public HttpComponentsClientHttpRequestFactory getAssociatedFactory(){
        return this.associatedHttpRequestFactory;
    }
}
