package com.wzm.aio.controller;


import com.wzm.aio.api.User;
import com.wzm.aio.service.MomoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class DemoController {

    public DemoController(MomoService momoService){
        this.momoService = momoService;
    }


    private final MomoService momoService;

    @RequestMapping("/test")
    public String test()  {
        boolean result = momoService.login(new User("441678514@qq.com", "wang998321"));

        return Boolean.toString(result);
    }
}
