package com.wzm.aio.controller;

import com.wzm.aio.pojo.vo.AddWordsRequestVO;
import com.wzm.aio.pojo.vo.AddWordsResultVO;
import com.wzm.aio.pojo.vo.Response;
import com.wzm.aio.properties.MomoProperties;
import com.wzm.aio.service.MomoService;
import com.wzm.aio.util.WordListParser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/momo")

public class MomoController {

    private final MomoService momoService;

    private final MomoProperties properties;

    public MomoController(MomoService momoService, MomoProperties properties) {
        this.momoService = momoService;
        this.properties = properties;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<AddWordsResultVO> addWords(@RequestBody AddWordsRequestVO words){
        System.out.println("收到请求，words = " + words);
        List<String> parsed = WordListParser.parse(words.getContent());
        int localId = properties.getNotepad().getLocalId();
        MomoService.AddWordsResult addWordsResult = momoService.addWordsToNotepad(localId, parsed);

        AddWordsResultVO result = AddWordsResultVO.builder().parsedWords(parsed)
                .newWords(addWordsResult.getNewWords())
                .existedWords(addWordsResult.getExistedWords()).build();
        return Response.ok(result);
    }

    @GetMapping("/pull")
    public Response<Void> pull(){
        momoService.pull();
        return Response.ok();
    }

}
