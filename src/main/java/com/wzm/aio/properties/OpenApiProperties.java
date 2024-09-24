package com.wzm.aio.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data

@ConfigurationProperties(prefix = "open-api")
public class OpenApiProperties {

    private MomoProperties momo;

    private FrDicProperties frDic;

    private String localBaseUrl;
    @Data
    public static class FrDicProperties {

        private String token;
        private String baseUrl;
    }

    @Data
    public static class MomoProperties {
        private String token;
        private String baseUrl;
        private int notepadMaxCount;

    }




}
