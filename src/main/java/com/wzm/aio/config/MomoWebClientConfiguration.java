package com.wzm.aio.config;

import com.wzm.aio.api.MomoOpenApi;
import com.wzm.aio.properties.MomoProperties;
import lombok.NonNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.*;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

import java.util.List;


@Configuration
public class MomoWebClientConfiguration {

    private final MomoProperties properties;

    private static final String BASE_URL = "https://open.maimemo.com/open/api/v1/notepads";

    public MomoWebClientConfiguration(MomoProperties properties) {
        this.properties = properties;
    }


    private void setDefaultHeaders(HttpHeaders headers){
        headers.setBearerAuth(properties.getToken());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    }

    @Bean
    MomoOpenApi demoApi() {
        WebClient webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeaders(this::setDefaultHeaders)
                .codecs(configurer
                        -> configurer.defaultCodecs()
                        .maxInMemorySize(1024 * 1024 * 2))
                .filter(new MomoApiLogFilter())
                .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(MomoOpenApi.class);
    }

    private static class MomoApiLogFilter implements ExchangeFilterFunction {
        private final Log logger = LogFactory.getLog(MomoApiLogFilter.class);

        @Override
        @NonNull
        public Mono<ClientResponse> filter(@NonNull ClientRequest request, @NonNull ExchangeFunction next) {
            logger.info("发起请求:" + request.logPrefix() + "HTTP " + request.method() + " " +
                    request.url() + ", headers=" + request.headers());
            return next.exchange(request);
        }
    }
}
