package com.wzm.aio.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.MultiValueMap;

@Data
@ConfigurationProperties(prefix = "open-api")
public class OpenApiProperties {

    private Momo momo;

    private FrDic frDic;

    private Openai openai;

    private String localBaseUrl;

    private int timeOut;
    private int maxMemorySize;
    private boolean enableLog;

    @Data
    public static class FrDic {
        private MultiValueMap<String,String> headers;
        private String baseUrl;
    }

    @Data
    public static class Momo {
        private MultiValueMap<String,String> headers;
        private String baseUrl;
        private int notepadMaxCount;

    }

    @Data
    public static class Openai{
        private String token;
        private String proxyHost;
        private int proxyPort;
        private String model;
        private double temperature;
        private int maxTokens;
    }




}
