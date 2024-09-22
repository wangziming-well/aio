package com.wzm.aio;

import com.wzm.aio.service.MomoService;
import com.wzm.aio.util.SpringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

@SpringBootTest
@TestConfiguration()
public class MomoServiceTest {

    @Autowired
    private MomoService service;

    @Autowired
    private ApplicationContext context;
    @BeforeTestMethod

    @Test
    public void pull(){
        SpringUtils.setApplicationContext(context);
        service.pull();
    }

    @Test
    public void push(){
        SpringUtils.setApplicationContext(context);
        service.push();
    }
}
