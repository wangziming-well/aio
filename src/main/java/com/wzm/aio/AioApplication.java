package com.wzm.aio;

import com.wzm.aio.pojo.model.Word;
import com.wzm.aio.pojo.vo.Response;
import com.wzm.aio.properties.MomoProperties;
import com.wzm.aio.service.MomoLocalService;
import com.wzm.aio.service.MomoService;
import com.wzm.aio.util.SpringUtils;
import com.wzm.aio.util.WordListParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan({ "com.wzm.aio.properties" })
public class AioApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(AioApplication.class, args);
        SpringUtils.setApplicationContext(run);
    }

}
