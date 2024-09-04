package com.wzm.aio.controller;


import com.wzm.aio.domain.MomoNotepadInfo;
import com.wzm.aio.service.MomoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class MomoController {

    private final MomoService momoService;

    public MomoController(MomoService momoService){

        this.momoService = momoService;
    }

    @PostMapping()
    public void getAllNotepad(){
        List<MomoNotepadInfo> allNotepad = momoService.getAllNotepad();

    }



}
