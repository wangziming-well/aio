package com.wzm.aio.controller;

import com.wzm.aio.api.frdic.FrWord;
import com.wzm.aio.pojo.vo.AddWordsResultVO;
import com.wzm.aio.pojo.vo.Response;
import com.wzm.aio.service.FrDicService;
import com.wzm.aio.service.MomoService;
import com.wzm.aio.util.TextParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/syncWord")
public class FrDicController {

    private final MomoService momoService;

    private final FrDicService frDicService;

    public FrDicController(MomoService momoService, FrDicService frDicService) {
        this.momoService = momoService;
        this.frDicService = frDicService;
    }

    @GetMapping
    public Response<AddWordsResultVO> syncWord(){
        List<FrWord> allWords = frDicService.getAllWords("0");
        List<String> wordList = allWords.stream().map(FrWord::getWord).toList();
        wordList = parseWords(wordList);
        Map<String, Boolean> code = momoService.addWordsToNotepad("Code", wordList);
        return Response.ok(AddWordsResultVO.fromMap(code));
    }

    private List<String> parseWords(List<String> words){
        List<TextParser.ResultEntry> parseResult = TextParser.parse(words)
                .stream().filter(new TextParser.ResultFilter()).toList();
        return parseResult.stream()
                .map(TextParser.ResultEntry::getLemma)
                .distinct()
                .sorted()
                .toList();

    }


}
