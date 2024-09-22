package com.wzm.aio.controller;

import com.wzm.aio.service.DocusaurusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/docusaurus")
public class DocusaurusController {

    private final DocusaurusService service;

    public DocusaurusController(DocusaurusService service) {
        this.service = service;
    }

    @GetMapping("/load")
    public String load(){
        service.loadDocusaurusWeb();
        return "ok";
    }

}
