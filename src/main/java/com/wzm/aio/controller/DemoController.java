package com.wzm.aio.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @RequestMapping("/test")
    public String test(){
        System.out.println("收到请求");
        return "copy";
    }
}
