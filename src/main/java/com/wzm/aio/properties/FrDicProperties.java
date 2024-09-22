package com.wzm.aio.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "frdic")
public class FrDicProperties {

    private String token;
}
