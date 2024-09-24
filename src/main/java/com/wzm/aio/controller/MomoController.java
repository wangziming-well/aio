package com.wzm.aio.controller;

import com.wzm.aio.pojo.dto.AddWordsDTO;
import com.wzm.aio.pojo.vo.AddWordsResultVO;
import com.wzm.aio.pojo.vo.Response;
import com.wzm.aio.service.MomoService;
import com.wzm.aio.util.TextParser;
import jakarta.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/momo")
public class MomoController {

    private final MomoService momoService;


    private static final Log logger = LogFactory.getLog(MomoController.class);


    public MomoController(MomoService momoService) {
        this.momoService = momoService;
    }

    private List<String> parseWords(String text) {
        List<TextParser.ResultEntry> parseResult = TextParser.parse(text)
                .stream().filter(new TextParser.ResultFilter()).toList();
        return parseResult.stream()
                .map(TextParser.ResultEntry::getLemma)
                .distinct()
                .sorted()
                .toList();
    }


    @PostMapping("/addWords")
    public Response<AddWordsResultVO> addWords(@RequestBody @Valid AddWordsDTO request) {
        logger.info("收到请求" + request);
        List<String> parsed = parseWords(request.getText());
        String title = request.getTitle();
        String localIdStr = request.getLocalId();
        int localId;
        if (StringUtils.hasText(title))
            localId = momoService.getNotepadId(title);
        else if (StringUtils.hasText(localIdStr))
            localId = Integer.parseInt(localIdStr);
        else
            return Response.error("title和localId至少有一个不为空");

        Map<String, Boolean> addWordsResult = momoService.addWordsToNotepad(localId, parsed);
        return Response.ok(AddWordsResultVO.fromMap(addWordsResult));
    }

    @GetMapping("/pull")
    public Response<Void> pull() {
        momoService.pull();
        return Response.ok();
    }

    @GetMapping("/push")
    public Response<Void> push() {
        momoService.push();
        return Response.ok();
    }

}
