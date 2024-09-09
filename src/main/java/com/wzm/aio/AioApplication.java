package com.wzm.aio;

import com.wzm.aio.util.SpringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan({ "com.wzm.aio.properties" })
public class AioApplication {


    public static void main(String[] args) throws ClassNotFoundException {
        ConfigurableApplicationContext run = SpringApplication.run(AioApplication.class, args);
        Class.forName("com.wzm.aio.util.TextParser");
        SpringUtils.setApplicationContext(run);
    }

}
