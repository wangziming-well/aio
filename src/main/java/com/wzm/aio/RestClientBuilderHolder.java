package com.wzm.aio;


import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

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
