package com.wzm.aio;

import com.wzm.aio.api.momo.MomoOpenApi;
import com.wzm.aio.api.momo.MomoResponse;
import com.wzm.aio.api.momo.dto.PhraseDTO;
import com.wzm.aio.api.momo.dto.VocabularyDTO;
import com.wzm.aio.api.momo.model.Notepad;
import com.wzm.aio.api.momo.model.Phrase;
import com.wzm.aio.service.MomoCloudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CloudMomoServiceTest {

    @Autowired
    private MomoCloudService service;

    @Autowired
    private MomoOpenApi api;

    /**
     * np-agXb6m4JwQaX2nnUHIC9h1cb7D2mH9V8sK4tu7KiYJfBMR0g5IypuoVQdmqJSXAO--default
     * np-FrQH_wPnaBeWN1XxCooxJm9OfEJIYNc1k7BAs3AXzyCAc3kNXdoxxkxHKBpkQiPN--Yes, Minister and Yes, Prime Minister
     * np-F6EsutIqJEXEGpa6KybpNF_YwukaPwMhphu09EEI6RVuQgd_kFtc-TXyb4fWygci--Code
     */
    @Test
    public void getAll(){
        List<Notepad> allNotepads = service.getAllNotepads();
        for (Notepad notepad : allNotepads){
            System.out.println(notepad.getId() + "--" + notepad.getTitle());
        }
    }
    @Test
    public void deleteNotepad(){
        service.deleteNotepad("np-FrQH_wPnaBeWN1XxCooxJm9OfEJIYNc1k7BAs3AXzyCAc3kNXdoxxkxHKBpkQiPN");
    }
    @Test
    public void updateNotepad(){
        Notepad notepad = service.getNotepad("np-F6EsutIqJEXEGpa6KybpNF_YwukaPwMhphu09EEI6RVuQgd_kFtc-TXyb4fWygci");
        notepad.setTitle("Code");
        Notepad newOne = service.updateNotepad(notepad);
        System.out.println(newOne);

    }
    //np-coTPDrrZ4uC4g-XYvZcqIkykC4rcL8HNncqkVX1oHHWDew6lvrTxzgpD0Za_mSzM

    @Test
    public void createNotepad(){
        Notepad build = Notepad.builder().title("test")
                .brief("test")
                .content("test")
                .build();
        Notepad notepad = service.createNotepad(build);
        System.out.println(notepad);

    }

    @Test
    public void getNotepad(){
        Notepad notepad = service.getNotepad("np-coTPDrrZ4uC4g-XYvZcqIkykD0Za_mSzM");
        System.out.println(notepad);

    }

    @Test
    public void demo(){
        MomoResponse<VocabularyDTO> apple = api.getVocabulary("apple");
        String vocId = apple.getData().getVoc().getId();
        Phrase phrase = new Phrase();
        phrase.setVocId(vocId);
        phrase.setTags(List.of("小学"));
        phrase.setPhrase("This is an apple.");
        phrase.setInterpretation("这是一个苹果222");
        phrase.setOrigin("小学");
        PhraseDTO phraseDTO = new PhraseDTO();
        phraseDTO.setPhrase(phrase);
        MomoResponse<PhraseDTO> response = api.createPhrase(phraseDTO);
        PhraseDTO data = response.getData();
        System.out.println(data);

    }


}
