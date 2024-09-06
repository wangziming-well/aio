package com.wzm.aio.api;

import com.wzm.aio.api.entity.OneNotepad;
import com.wzm.aio.api.entity.NotepadList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;


/**
 * 墨墨背单词HTTP API
 */
@HttpExchange
public interface MomoOpenApi {

    @GetExchange
    ResponseEntity<MomoResponse<NotepadList>> getAllNotepads();

    @PostExchange
    ResponseEntity<MomoResponse<OneNotepad>> createNotepad(@RequestBody OneNotepad input);

    @GetExchange("/{id}")
    ResponseEntity<MomoResponse<OneNotepad>> getNotepad(@PathVariable String id);

    @DeleteExchange("/{id}")
    ResponseEntity<MomoResponse<Void>> deleteNotepad(@PathVariable String id);

    @PostExchange("/{id}")
    ResponseEntity<MomoResponse<OneNotepad>> updateNotepad(@PathVariable String id,@RequestBody OneNotepad input);

}
