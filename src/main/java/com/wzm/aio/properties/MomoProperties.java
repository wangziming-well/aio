package com.wzm.aio.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "momo")
public class MomoProperties {

    private String token;
    private final NotepadProperties notepad;

    public MomoProperties(){
        this.notepad = new NotepadProperties();
    }


    @Data
    public static class NotepadProperties{
        private String cloudId;
        private int localId;
        private String title;
        private String brief;
        private int maxCount;
    }


}
