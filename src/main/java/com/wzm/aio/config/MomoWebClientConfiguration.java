package com.wzm.aio.config;

import com.wzm.aio.api.MomoApi;
import com.wzm.aio.service.MomoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class MomoWebClientConfiguration {


    private final MomoService momoService;
    public MomoWebClientConfiguration(MomoService momoService){

        this.momoService = momoService;
    }

    @Bean
    MomoApi demoApi() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://www.maimemo.com")
                .codecs(configurer
                        -> configurer.defaultCodecs()
                        .maxInMemorySize(1024*1024*2))
                .filter(momoService.new CookiesFilter())
                .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(MomoApi.class);
    }
}
