package com.wzm.aio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan({ "com.wzm.aio.properties" })
public class AioApplication {


    public static void main(String[] args) {
        SpringApplication.run(AioApplication.class, args);
    }

}
