package com.wzm.aio.api.momo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wzm.aio.config.AutoMapperConfiguration;
import com.wzm.aio.pojo.model.MomoLocalNotepad;
import com.wzm.aio.util.BeanUtils;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AutoMapper(target = MomoLocalNotepad.class,
        uses = AutoMapperConfiguration.StringListConverter.class)
public class Notepad {
    public enum Type {
        FAVORITE, NOTEPAD
    }

    public enum Stats {
        PUBLISHED, UNPUBLISHED, DELETE
    }
    @AutoMapping(target = "id",ignore = true)
    private String id;
    private Type type;
    private Stats status = Stats.PUBLISHED;
    @AutoMapping(target = "words")
    private String content;
    private String title;
    private String brief;
    private List<String> tags = List.of(" ");
    private List<NotepadItem> list;
    @JsonProperty("created_time")
    private OffsetDateTime createdTime;
    @JsonProperty("updated_time")
    private OffsetDateTime updatedTime;


    public MomoLocalNotepad toLocal(){
        MomoLocalNotepad local = BeanUtils.convert(this, MomoLocalNotepad.class);
        local.setCloudId(this.id);
        return local;
    }

    public static class Builder {
        private final Notepad notepad = new Notepad();

        public Builder id(String id) {
            this.notepad.setId(id);
            return this;
        }

        public Builder status(Stats stats) {
            this.notepad.setStatus(stats);
            return this;
        }

        public Builder title(String title) {
            this.notepad.setTitle(title);
            return this;
        }

        public Builder brief(String brief) {
            this.notepad.setBrief(brief);
            return this;
        }

        public Builder content(String content) {
            this.notepad.setContent(content);
            return this;
        }

        public Builder tags(String... tags) {
            this.notepad.setTags(List.of(tags));
            return this;
        }

        public Notepad build() {
            return this.notepad;
        }

    }

    public static Builder builder() {
        return new Builder();
    }


    @Data
    public static class NotepadItem {
        private String type;
        private String chapter;
        private String word;
    }


}