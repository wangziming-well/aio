package com.wzm.aio;

import com.wzm.aio.api.frdic.*;
import com.wzm.aio.service.FrDicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@SpringBootTest
class AioApplicationTests {


    @Autowired
    FrDicOpenApi api;

    @Autowired
    FrDicService service;

    @Autowired
    ResourceLoader resourceLoader;


    @Test
    void contextLoads() {
        List<FrWord> allWords = service.getAllWords("0");
        System.out.println(allWords.size());
        System.out.println(allWords);

    }
    @Test
    void testResource() throws IOException {
        URL resource = FrDicApiAccessFrequencyAspect.class.getClassLoader().getResource("classpath:serialized");
        System.out.println(resource);
    }

}
