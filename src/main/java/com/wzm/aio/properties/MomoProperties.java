package com.wzm.aio.properties;


import com.wzm.aio.domain.MomoNotepad;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("momo")
@Data
public class MomoProperties {

    private String token;


}
