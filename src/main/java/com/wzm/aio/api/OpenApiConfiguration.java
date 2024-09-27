package com.wzm.aio.api;

import com.wzm.aio.api.frdic.FrDicOpenApi;
import com.wzm.aio.api.local.LocalOpenApi;
import com.wzm.aio.api.momo.MomoOpenApi;
import com.wzm.aio.properties.OpenApiProperties;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.common.OpenAiApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.time.Duration;

@Configuration
public class OpenApiConfiguration {

    private final OpenApiProperties properties;

    public OpenApiConfiguration(OpenApiProperties properties) {
        this.properties = properties;
    }
    //todo 研究为WebClient复用创建api时报错的问题
    @Bean
    @Scope("prototype")
    public WebClient.Builder webClientBuilder() {
        int maxMemorySize = properties.getMaxMemorySize();
        int timeOut = properties.getTimeOut();
        boolean enableLog = properties.isEnableLog();
        WebClient.Builder builder = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(maxMemorySize * 1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(timeOut))));
        if (enableLog)
            builder.filter(WebClientLogFilter.logFilter());
        return builder;
    }
    @Bean
    public RestClient.Builder restClientBuilder(RestClientBuilderHolder restClientBuilderHolder) {
        return restClientBuilderHolder.get();
    }

    @Bean
    public RestClientBuilderHolder restClientBuilderHolder(){
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(properties.getTimeOut()));
        RestClient.Builder builder = RestClient.builder();
        builder.requestInterceptor(new RestClientLogFilter());
        return new RestClientBuilderHolder(builder,factory);
    }


    private <T> T createClient(WebClient webClient, Class<T> clazz) {
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(clazz);
    }

    private <T> T createOpenApiClient(WebClient.Builder webClientBuilder, String baseUrl, MultiValueMap<String,String> headers, Class<T> apiClass) {
        if (headers != null)
            webClientBuilder.defaultHeaders(defaultHeaders -> defaultHeaders.addAll(headers));
        WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();
        return createClient(webClient, apiClass);
    }

    @Bean
    public MomoOpenApi momoOpenApi(WebClient.Builder webClientBuilder) {
        OpenApiProperties.Momo momo = properties.getMomo();
        return createOpenApiClient(webClientBuilder,momo.getBaseUrl(),momo.getHeaders(), MomoOpenApi.class);
    }
    @Bean
    public FrDicOpenApi frDicOpenApi(WebClient.Builder webClientBuilder) {
        OpenApiProperties.FrDic frDic = properties.getFrDic();
        return createOpenApiClient(webClientBuilder,frDic.getBaseUrl(),frDic.getHeaders(), FrDicOpenApi.class);
    }

    @Bean
    public LocalOpenApi localOpenApi(WebClient.Builder webClientBuilder) {
        return createOpenApiClient(webClientBuilder,properties.getLocalBaseUrl(),null, LocalOpenApi.class);
    }


    @Bean
    public OpenAiChatModel openAiChatModel(WebClient.Builder webClientBuilder,RestClientBuilderHolder restClientBuilderHolder) {
        OpenApiProperties.Openai openai = properties.getOpenai();
        String proxyHost = openai.getProxyHost();
        int proxyPort = openai.getProxyPort();

        setProxy(webClientBuilder, proxyHost, proxyPort);
        setProxy(restClientBuilderHolder,proxyHost, proxyPort);
        OpenAiApi openAiApi = new OpenAiApi(OpenAiApiConstants.DEFAULT_BASE_URL, openai.getToken(), restClientBuilderHolder.get(), webClientBuilder);
        return new OpenAiChatModel(openAiApi, openai.toOpenAiChatOptions());
    }


    private void setProxy(RestClientBuilderHolder restClientBuilderHolder,String proxyHost, int proxyPort) {
        HttpComponentsClientHttpRequestFactory requestFactory = restClientBuilderHolder.getAssociatedFactory();

        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        CloseableHttpClient closeableHttpClient = HttpClients.custom()
                .setRoutePlanner(routePlanner)
                .build();
        requestFactory.setHttpClient(closeableHttpClient);
    }

    private void setProxy(WebClient.Builder webClientBuilder, String proxyHost, int proxyPort) {
        HttpClient httpClient = HttpClient.create()
                .proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                        .host(proxyHost).port(proxyPort));
        webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
    }

}
