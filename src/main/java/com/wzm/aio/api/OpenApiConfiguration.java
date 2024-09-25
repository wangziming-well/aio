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
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.common.OpenAiApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    public WebClient.Builder createWebClientBuilder() {
        int maxMemorySize = properties.getMaxMemorySize();
        int timeOut = properties.getTimeOut();
        boolean enableLog = properties.isEnableLog();
        WebClient.Builder builder = WebClient.builder()
                .codecs(configurer
                        -> configurer.defaultCodecs()
                        .maxInMemorySize(maxMemorySize * 1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(timeOut))));
        if (enableLog)
            builder.filter(ApiFilter.logFilter());
        return builder;
    }


    private <T> T createClient(WebClient webClient, Class<T> clazz) {
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(clazz);
    }

    private <T> T createOpenApiClient( String baseUrl, MultiValueMap<String,String> headers, Class<T> apiClass) {
        WebClient webClient = createWebClientBuilder().baseUrl(baseUrl).defaultHeaders(defaultHeaders -> defaultHeaders.addAll(headers)).build();
        return createClient(webClient, apiClass);
    }

    private <T> T createOpenApiClient(String baseUrl, Class<T> apiClass) {
        WebClient webClient = createWebClientBuilder().baseUrl(baseUrl).build();
        return createClient(webClient, apiClass);
    }

    @Bean
    public MomoOpenApi momoOpenApi() {
        OpenApiProperties.Momo momo = properties.getMomo();
        return createOpenApiClient(momo.getBaseUrl(),momo.getHeaders(), MomoOpenApi.class);
    }
    @Bean
    public FrDicOpenApi frDicOpenApi() {
        OpenApiProperties.FrDic frDic = properties.getFrDic();
        return createOpenApiClient(frDic.getBaseUrl(),frDic.getHeaders(), FrDicOpenApi.class);
    }

    @Bean
    public LocalOpenApi localOpenApi() {
        return createOpenApiClient(properties.getLocalBaseUrl(), LocalOpenApi.class);
    }


    @Bean
    public OpenAiChatModel openAiChatModel(WebClient.Builder webClientBuilder) {

        OpenApiProperties.Openai openai = properties.getOpenai();
        String proxyHost = openai.getProxyHost();
        int proxyPort = openai.getProxyPort();

        webClientBuilder = setProxy(webClientBuilder, proxyHost, proxyPort);
        RestClient.Builder restClientBuilder = restClientBuilder(proxyHost, proxyPort);
        OpenAiApi openAiApi = new OpenAiApi(OpenAiApiConstants.DEFAULT_BASE_URL, openai.getToken(), restClientBuilder, webClientBuilder);
        return new OpenAiChatModel(openAiApi, from(openai));
    }

    private OpenAiChatOptions from(OpenApiProperties.Openai openai) {
        return OpenAiChatOptions.builder()
                .withModel(openai.getModel())
                .withTemperature(openai.getTemperature())
                .withMaxTokens(openai.getMaxTokens())
                .build();
    }

    private RestClient.Builder restClientBuilder(String proxyHost, int proxyPort) {
        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        CloseableHttpClient closeableHttpClient = HttpClients.custom()
                .setRoutePlanner(routePlanner)
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(closeableHttpClient);
        factory.setConnectTimeout(Duration.ofSeconds(properties.getTimeOut()));
        return RestClient.builder().requestFactory(factory);
    }

    private WebClient.Builder setProxy(WebClient.Builder webClientBuilder, String proxyHost, int proxyPort) {
        HttpClient httpClient = HttpClient.create()
                .proxy(proxy -> proxy
                        .type(ProxyProvider.Proxy.HTTP)
                        .host(proxyHost)
                        .port(proxyPort));
        return webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
    }

}
