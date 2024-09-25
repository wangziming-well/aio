package com.wzm.aio.api.openai;

import com.wzm.aio.properties.OpenApiProperties;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.common.OpenAiApiConstants;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

@Component
public class OpenAiOpenApi {

    private final OpenApiProperties.Openai openaiProperties;

    private final OpenAiChatModel chatModel;

    public OpenAiOpenApi(OpenApiProperties properties) {
        this.openaiProperties = properties.getOpenai();
        this.chatModel = openAiChatModel();
    }

    private WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .proxy(proxy -> proxy
                        .type(ProxyProvider.Proxy.HTTP)
                        .host("localhost")
                        .port(7890));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    private RestClient.Builder restClientBuilder(){
        HttpHost proxy = new HttpHost("localhost", 7890);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        CloseableHttpClient closeableHttpClient = HttpClients.custom()
                .setRoutePlanner(routePlanner)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(closeableHttpClient);
        return RestClient.builder().requestFactory(factory);
    }

    private OpenAiChatModel openAiChatModel(){

        String token = openaiProperties.getToken();
        OpenAiApi openAiApi = new OpenAiApi(OpenAiApiConstants.DEFAULT_BASE_URL, token, restClientBuilder(), webClientBuilder());
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .withModel("gpt-3.5-turbo")
                .withTemperature(0.4)
                .withMaxTokens(200)
                .build();
        return new OpenAiChatModel(openAiApi, openAiChatOptions);
    }

    public void test() {


    }
}
