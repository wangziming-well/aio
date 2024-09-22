package com.wzm.aio.config;

import com.wzm.aio.api.frdic.FrDicOpenApi;

import com.wzm.aio.properties.FrDicProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

@Configuration
public class FrDicWebClientConfiguration {

    private final FrDicProperties properties;

    private static final String BASE_URL = "https://api.frdic.com/api/open/v1/studylist";

    public FrDicWebClientConfiguration(FrDicProperties properties) {
        this.properties = properties;
    }

    @Bean
    FrDicOpenApi frDicOpenApi() {
        WebClient webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeaders(this::setDefaultHeaders)
                .codecs(configurer
                        -> configurer.defaultCodecs()
                        .maxInMemorySize(1024 * 1024 * 2))
                .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(FrDicOpenApi.class);
    }

    private void setDefaultHeaders(HttpHeaders headers) {
        headers.set(HttpHeaders.AUTHORIZATION, properties.getToken());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    }


}
