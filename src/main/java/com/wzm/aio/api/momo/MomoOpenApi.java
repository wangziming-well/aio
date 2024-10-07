package com.wzm.aio.api.momo;

import com.wzm.aio.api.momo.dto.NotepadDTO;
import com.wzm.aio.api.momo.dto.NotepadsDTO;
import com.wzm.aio.api.momo.dto.PhraseDTO;
import com.wzm.aio.api.momo.dto.VocabularyDTO;
import com.wzm.aio.api.momo.model.Phrase;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetExchange("/vocabulary")
    MomoResponse<VocabularyDTO> getVocabulary(@RequestParam String spelling);

    @GetExchange("/notepads")
    MomoResponse<NotepadsDTO> getAllNotepads();

    @PostExchange("/notepads")
    MomoResponse<NotepadDTO> createNotepad(@RequestBody NotepadDTO input);

    @GetExchange("/notepads/{id}")
    MomoResponse<NotepadDTO> getNotepad(@PathVariable String id);

    @DeleteExchange("/notepads/{id}")
    MomoResponse<Void> deleteNotepad(@PathVariable String id);

    @PostExchange("/notepads/{id}")
    MomoResponse<NotepadDTO> updateNotepad(@PathVariable String id, @RequestBody NotepadDTO input);

    @PostExchange("/phrases")
    MomoResponse<PhraseDTO> createPhrase(@RequestBody PhraseDTO input);

    @PostExchange("/notepads/{id}")
    MomoResponse<PhraseDTO> changePhrase(@PathVariable String id,@RequestBody PhraseDTO input);


}
