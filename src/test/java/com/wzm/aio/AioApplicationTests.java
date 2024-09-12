package com.wzm.aio;

import com.wzm.aio.properties.DocusaurusProperties;
import com.wzm.aio.service.DocusaurusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AioApplicationTests {


    @Autowired
    DocusaurusService service;



    @Test
    void contextLoads() {
        service.loadNotePicture();
    }

}
