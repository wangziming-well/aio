package com.wzm.aio;

import com.wzm.aio.pojo.model.MomoCloudNotepad;
import com.wzm.aio.service.MomoCloudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CloudMomoServiceTest {

    @Autowired
    private MomoCloudService service;

    /**
     * np-agXb6m4JwQaX2nnUHIC9h1cb7D2mH9V8sK4tu7KiYJfBMR0g5IypuoVQdmqJSXAO--default
     * np-FrQH_wPnaBeWN1XxCooxJm9OfEJIYNc1k7BAs3AXzyCAc3kNXdoxxkxHKBpkQiPN--Yes, Minister and Yes, Prime Minister
     * np-F6EsutIqJEXEGpa6KybpNF_YwukaPwMhphu09EEI6RVuQgd_kFtc-TXyb4fWygci--Code
     */
    @Test
    public void getAll(){
        List<MomoCloudNotepad> allNotepads = service.getAllNotepads();
        for (MomoCloudNotepad momoCloudNotepad : allNotepads){
            System.out.println(momoCloudNotepad.getId() + "--" + momoCloudNotepad.getTitle());
        }
    }
    @Test
    public void deleteNotepad(){
        service.deleteNotepad("np-FrQH_wPnaBeWN1XxCooxJm9OfEJIYNc1k7BAs3AXzyCAc3kNXdoxxkxHKBpkQiPN");
    }
    @Test
    public void updateNotepad(){
        MomoCloudNotepad notepad = service.getNotepad("np-F6EsutIqJEXEGpa6KybpNF_YwukaPwMhphu09EEI6RVuQgd_kFtc-TXyb4fWygci");
        notepad.setTitle("Code");
        MomoCloudNotepad momoCloudNotepad = service.updateNotepad(notepad);
        System.out.println(momoCloudNotepad);

    }
    //np-coTPDrrZ4uC4g-XYvZcqIkykC4rcL8HNncqkVX1oHHWDew6lvrTxzgpD0Za_mSzM

    @Test
    public void createNotepad(){
        MomoCloudNotepad build = MomoCloudNotepad.builder().title("test")
                .brief("test")
                .content("test")
                .build();
        MomoCloudNotepad notepad = service.createNotepad(build);
        System.out.println(notepad);

    }

    @Test
    public void getNotepad(){
        MomoCloudNotepad notepad = service.getNotepad("np-coTPDrrZ4uC4g-XYvZcqIkykD0Za_mSzM");
        System.out.println(notepad);

    }


}
