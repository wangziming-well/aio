package com.wzm.aio.config;

import com.wzm.aio.api.MomoApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.URI;

@Configuration
public class WebClientConfiguration {

    @Bean
    MomoApi demoApi() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://www.maimemo.com")
                .codecs(configurer
                        -> configurer.defaultCodecs()
                        .maxInMemorySize(1024*1024*2))
                .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(MomoApi.class);
    }
}
