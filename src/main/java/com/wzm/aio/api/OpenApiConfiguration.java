package com.wzm.aio.api;

import com.wzm.aio.api.frdic.FrDicOpenApi;
import com.wzm.aio.api.local.LocalOpenApi;
import com.wzm.aio.api.momo.MomoOpenApi;
import com.wzm.aio.properties.OpenApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    private final OpenApiProperties properties;

    public OpenApiConfiguration(OpenApiProperties properties) {
        this.properties = properties;
    }


    @Bean
    public MomoOpenApi momoOpenApi(){
        OpenApiProperties.Momo momo = properties.getMomo();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(momo.getToken());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return OpenApiBuilder.builder(momo.getBaseUrl(),MomoOpenApi.class)
                .defaultHeaders(headers)
                .addFilter(ApiFilter.logFilter())
                .build();
    }

    @Bean
    public FrDicOpenApi frDicOpenApi(){
        OpenApiProperties.FrDic frDic = properties.getFrDic();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, frDic.getToken());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return OpenApiBuilder.builder(frDic.getBaseUrl(),FrDicOpenApi.class)
                .defaultHeaders(headers)
                .addFilter(ApiFilter.logFilter())
                .build();
    }
    @Bean
    public LocalOpenApi localOpenApi(){
        String localBaseUrl = properties.getLocalBaseUrl();
        return OpenApiBuilder.builder(localBaseUrl,LocalOpenApi.class)
                .addFilter(ApiFilter.logFilter())
                .build();
    }


}
