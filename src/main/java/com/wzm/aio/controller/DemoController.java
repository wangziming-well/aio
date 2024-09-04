package com.wzm.aio.controller;


import com.wzm.aio.domain.User;
import com.wzm.aio.service.MomoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    public DemoController(MomoService momoService){
        this.momoService = momoService;
    }


    private final MomoService momoService;

    @RequestMapping("/test")
    public String test()  {
        boolean result = momoService.login(new User("441678514@qq.com", "wang998321"));
        if (result) {
            momoService.getAllNotepad();
        }
        return Boolean.toString(result);
    }
}
