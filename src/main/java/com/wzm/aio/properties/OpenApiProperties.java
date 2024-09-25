package com.wzm.aio.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "open-api")
public class OpenApiProperties {

    private Momo momo;

    private FrDic frDic;

    private Openai openai;

    private String localBaseUrl;

    @Data
    public static class FrDic {

        private String token;
        private String baseUrl;
    }

    @Data
    public static class Momo {
        private String token;
        private String baseUrl;
        private int notepadMaxCount;

    }

    @Data
    public static class Openai{
        private String token;
        private String proxyHost;
        private int proxyPort;
    }




}
