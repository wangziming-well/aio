package com.wzm.aio.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class OpenAiService {

    private final OpenAiChatModel chatModel;

    public OpenAiService(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public void test(){
        ChatResponse test = chatModel.call(new Prompt("test"));
        String string = test.getResult().getOutput().toString();
        System.out.println(string);
    }

}
