package com.wzm.aio.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("momo")
@Data
public class MomoProperties {

    private String username;
    private String password;
    private String notepadId;

}
