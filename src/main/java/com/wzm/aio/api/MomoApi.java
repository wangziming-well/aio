package com.wzm.aio.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.Map;

@HttpExchange
public interface MomoApi {
    @PostExchange(value = "/auth/login" ,contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<String> login(@RequestParam Map<String, String> user);

    @PostExchange(value = "/test")
    ResponseEntity<String> post(@CookieValue Map<String,String> cookies);

}
