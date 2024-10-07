package com.wzm.aio.api.frdic;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.*;

import java.util.List;
import java.util.Map;

@HttpExchange
public interface FrDicOpenApi {

    @GetExchange("/category")
    FrDicResponse<List<FrWordsBook>> getAllWordsBooks(@RequestParam("language") String language);

    @PostExchange("/category")
    FrDicResponse<FrWordsBook> addWordsBook(@RequestBody FrWordsBook request);

    @PatchExchange("/category")
    FrDicResponse<Void> changeWordsBook(@RequestBody FrWordsBook request);

    //返回null 表示删除成功 抛出异常表示删除失败
    @DeleteExchange("/category")
    Void deleteWordsBook(@RequestBody FrWordsBook request);

    @GetExchange("/words/{id}")
    FrDicResponse<List<FrWord>> getWords(@PathVariable("id") String wordsBookId,
                                         @RequestParam String language,
                                         @RequestParam Integer page,
                                         @RequestParam("page_size") Integer pageSize);

    @PostExchange("/words")
    FrDicResponse<Void> addWords(@RequestBody FrWordsBook frWordsBook);

    @DeleteExchange("/words")
    Void deleteWords(@RequestBody FrWordsBook frWordsBook);
}
