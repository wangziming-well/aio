package com.wzm.aio.api.entity;

import com.wzm.aio.api.entity.Notepad;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.List;


@Data
public class OneNotepad {

    private Notepad notepad;

    public static Builder toUpdate(){
        return new Builder();
    }

    public static class Builder{
        private final OneNotepad notepadHolder = new OneNotepad();
        private final Notepad notepad = new Notepad();


        public Builder status(Notepad.Stats stats){
            this.notepad.setStatus(stats);
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

        public Builder tags(String... tags){
            this.notepad.setTags(List.of(tags));
            return this;
        }

        public OneNotepad build(){
            checkRequired();
            this.notepadHolder.setNotepad(this.notepad);
            return this.notepadHolder;
        }
        public void checkRequired(){
            Assert.notNull(this.notepad.getTitle(),"title不能为空");
            Assert.notNull(this.notepad.getContent(),"content不能为空");
            Assert.notNull(this.notepad.getStatus(),"status不能为空");
            Assert.notNull(this.notepad.getBrief(),"brief不能为空");
            Assert.notNull(this.notepad.getTags(),"tags不能为空");
        }

    }



}
