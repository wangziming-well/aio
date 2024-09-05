package com.wzm.aio.config;

import com.wzm.aio.api.MomoApi;
import com.wzm.aio.domain.MomoCookies;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.*;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;


@Configuration
public class MomoWebClientConfiguration {

    @Bean
    MomoApi demoApi() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://www.maimemo.com")
                .codecs(configurer
                        -> configurer.defaultCodecs()
                        .maxInMemorySize(1024*1024*2))
                .filter(new CookiesFilter())
                .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(MomoApi.class);
    }
    //为默默背单词HTTP请求添加cookies
    private static class CookiesFilter implements ExchangeFilterFunction {
        @Override
        @NonNull
        public Mono<ClientResponse> filter(@NonNull ClientRequest request,@NonNull ExchangeFunction next) {
            if (MomoCookies.INSTANCE.isEmpty())
                return next.exchange(request);
            MultiValueMap<String, String> cookiesMap = MomoCookies.INSTANCE.getCookiesMap();
            ClientRequest filtered = ClientRequest.from(request).cookies(cookies -> cookies.addAll(cookiesMap)).build();
            return next.exchange(filtered);
        }
    }
}
