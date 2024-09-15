package com.wzm.aio.controller;

import com.wzm.aio.api.MomoOpenApiAspect;
import com.wzm.aio.pojo.vo.AddWordsRequestVO;
import com.wzm.aio.pojo.vo.AddWordsResultVO;
import com.wzm.aio.pojo.vo.Response;
import com.wzm.aio.properties.MomoProperties;
import com.wzm.aio.service.MomoService;
import com.wzm.aio.util.TextParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/momo")
public class MomoController {

    private final MomoService momoService;

    private final MomoProperties properties;

    private static final Log logger = LogFactory.getLog(MomoController.class);


    public MomoController(MomoService momoService, MomoProperties properties) {
        this.momoService = momoService;
        this.properties = properties;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<AddWordsResultVO> addWords(@RequestBody AddWordsRequestVO words){
        logger.info("收到请求，text=" + words.getText());
        List<TextParser.ResultEntry> parseResult = TextParser.parse(words.getText())
                .stream().filter(new TextParser.ResultFilter()).toList();
        List<String> parsed = parseResult.stream()
                .map(TextParser.ResultEntry::getLemma)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        for (TextParser.ResultEntry entry :parseResult){
            System.out.printf("%-20s %-20s %-15s %-15s\n",entry.getWord(),entry.getLemma(),entry.getPos(),entry.getPosTag());
        }

        int localId = properties.getNotepad().getLocalId();
        MomoService.AddWordsResult addWordsResult = momoService.addWordsToNotepad(localId, parsed);
        List<String> newWords = addWordsResult.getNewWords();
        List<AddWordsResultVO.ResultEntry> collect = parsed.stream().map(word -> {
            AddWordsResultVO.ResultEntry resultEntry = new AddWordsResultVO.ResultEntry(word);
            for (String newWord : newWords) {
                if (Objects.equals(word, newWord))
                    resultEntry.setNewWords(true);
            }
            return resultEntry;
        }).toList();
        return Response.ok(new AddWordsResultVO(collect));
    }


    @GetMapping("/pull")
    public Response<Void> pull(){
        momoService.pull();
        return Response.ok();
    }

}
