package com.wzm.aio.controller;

import com.wzm.aio.api.openai.OpenAiOpenApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/openai")
public class OpenAiController {

    private final OpenAiOpenApi api;

    public OpenAiController(OpenAiOpenApi api) {
        this.api = api;
    }


    @GetMapping("/test")
    public String test(){
        api.test();
        return "ojbk";
    }

}
