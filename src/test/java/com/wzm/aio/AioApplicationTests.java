package com.wzm.aio;

import com.wzm.aio.properties.DocusaurusProperties;
import com.wzm.aio.service.DocusaurusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;

@SpringBootTest
class AioApplicationTests {


    @Autowired
    DocusaurusService service;

    @Autowired
    ServerProperties properties;



    @Test
    void contextLoads() {
    }

}
