package com.wzm.aio;

import com.wzm.aio.service.JavService;
import com.wzm.aio.util.JacksonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class JavTest {
    @Autowired
    private JavService service;

    @Test
    public void mainTest(){
        ArrayList<JavService.VideoItem> videoItems = service.allWatchedVideo();
        System.out.println(JacksonUtils.toJsonString(videoItems));
    }




}
