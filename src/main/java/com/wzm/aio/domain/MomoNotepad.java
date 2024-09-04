package com.wzm.aio.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MomoNotepad {
    private String id;
    private String title;
    private String brief;
    private String content;
    @JsonProperty("is_private")
    private boolean isPrivate;

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {

        private final MomoNotepad notepad;
        public Builder(){
            this.notepad = new MomoNotepad();
        }

        public Builder id(String id){
            this.notepad.setId(id);
            return this;
        }
        public Builder title(String title){
            this.notepad.setTitle(title);
            return this;
        }
        public Builder brief(String brief){
            this.notepad.setBrief(brief);
            return this;
        }

        public Builder content(String content){
            this.notepad.setContent(content);
            return this;
        }

        public Builder isPrivate(boolean isPrivate){
            this.notepad.setPrivate(isPrivate);
            return this;
        }

        public MomoNotepad build(){
            return this.notepad;
        }

    }

}
