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

    @PostMapping(consumes = MediaType.ALL_VALUE)
    public Response<AddWordsResultVO> addWords(@RequestParam String words){
        AddWordsResultVO result = new AddWordsResultVO();
        List<String> parsed = WordListParser.parse(words);
        int localId = properties.getNotepad().getLocalId();
        MomoService.AddWordsResult addWordsResult = momoService.addWordsToNotepad(localId, parsed);
        result.setParsedWords(parsed);
        result.setNewWords(addWordsResult.getNewWords());
        result.setExistedWords(addWordsResult.getExistedWords());
        return Response.ok(result);
    }

}
