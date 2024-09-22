package com.wzm.aio.controller;

import com.wzm.aio.pojo.dto.AddWordsDTO;
import com.wzm.aio.pojo.vo.AddWordsResultVO;
import com.wzm.aio.pojo.vo.Response;
import com.wzm.aio.properties.MomoProperties;
import com.wzm.aio.service.MomoService;
import com.wzm.aio.util.TextParser;
import jakarta.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private List<String> parseWords(String text){
        List<TextParser.ResultEntry> parseResult = TextParser.parse(text)
                .stream().filter(new TextParser.ResultFilter()).toList();
        List<String> parsed = parseResult.stream()
                .map(TextParser.ResultEntry::getLemma)
                .distinct()
                .sorted()
                .toList();

        for (TextParser.ResultEntry entry :parseResult){
            System.out.printf("%-20s %-20s %-15s %-15s\n",entry.getWord(),entry.getLemma(),entry.getPos(),entry.getPosTag());
        }
        return  parsed;

    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<AddWordsResultVO> addWords(@RequestBody @Valid AddWordsDTO request){
        logger.info("收到请求" + request);
        List<String> parsed = parseWords(request.getText());
        String title = request.getTitle();
        String localIdStr = request.getLocalId();
        int localId = properties.getNotepad().getLocalId();
        if (StringUtils.hasText(title))
            localId = momoService.getNotepadId(title);
        if (StringUtils.hasText(localIdStr))
            localId = Integer.parseInt(localIdStr);

        Map<String, Boolean> addWordsResult = momoService.addWordsToNotepad(localId, parsed);
        return Response.ok(AddWordsResultVO.fromMap(addWordsResult));
    }

    @GetMapping("/pull")
    public Response<Void> pull(){
        momoService.pull();
        return Response.ok();
    }

}
