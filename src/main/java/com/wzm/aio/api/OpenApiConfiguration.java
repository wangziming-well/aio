package com.wzm.aio.api;

import com.wzm.aio.api.frdic.FrDicOpenApi;
import com.wzm.aio.api.local.LocalOpenApi;
import com.wzm.aio.api.momo.MomoOpenApi;
import com.wzm.aio.properties.FrDicProperties;
import com.wzm.aio.properties.MomoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@Configuration
public class OpenApiConfiguration {
    private final MomoProperties momoProperties;
    private final FrDicProperties frDicProperties;

    public OpenApiConfiguration(MomoProperties momoProperties, FrDicProperties frDicProperties) {
        this.momoProperties = momoProperties;
        this.frDicProperties = frDicProperties;
    }


    @Bean
    public MomoOpenApi momoOpenApi(){
        String baseUrl = "https://open.maimemo.com/open/api/v1/notepads";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(momoProperties.getToken());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return OpenApiBuilder.builder(baseUrl,MomoOpenApi.class)
                .defaultHeaders(headers)
                .addFilter(ApiLoggingFilter.logRequest())
                .addFilter(ApiLoggingFilter.logResponse())
                .addFilter(ApiLoggingFilter.dealException())
                .build();
    }

    @Bean
    public FrDicOpenApi frDicOpenApi(){
        String baseUrl = "https://api.frdic.com/api/open/v1/studylist";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, frDicProperties.getToken());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return OpenApiBuilder.builder(baseUrl,FrDicOpenApi.class)
                .defaultHeaders(headers)
                .addFilter(ApiLoggingFilter.logRequest())
                .addFilter(ApiLoggingFilter.logResponse())
                .addFilter(ApiLoggingFilter.dealException())
                .build();
    }
    @Bean
    public LocalOpenApi localOpenApi(){
        String baseUrl = "http://localhost:8080/";
        return OpenApiBuilder.builder(baseUrl,LocalOpenApi.class)
                .addFilter(ApiLoggingFilter.logRequest())
                .addFilter(ApiLoggingFilter.logResponse())
                .addFilter(ApiLoggingFilter.dealException())
                .build();
    }


}
