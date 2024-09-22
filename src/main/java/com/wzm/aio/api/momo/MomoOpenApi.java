package com.wzm.aio.api.momo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;


/**
 * 墨墨背单词HTTP API
 * MomoOpenApi保证方法返回的ResponseEntity，其getBody()不为null
 */
@HttpExchange
public interface MomoOpenApi {

    @GetExchange
    MomoResponse<NotepadList> getAllNotepads();

    @PostExchange
    MomoResponse<OneNotepad> createNotepad(@RequestBody OneNotepad input);

    @GetExchange("/{id}")
    MomoResponse<OneNotepad> getNotepad(@PathVariable String id);

    @DeleteExchange("/{id}")
    MomoResponse<Void> deleteNotepad(@PathVariable String id);

    @PostExchange("/{id}")
    MomoResponse<OneNotepad> updateNotepad(@PathVariable String id,@RequestBody OneNotepad input);

}
