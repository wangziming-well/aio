package com.wzm.aio.pojo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wzm.aio.config.AutoMapperConfiguration;
import com.wzm.aio.util.BeanUtils;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AutoMapper(target = MomoLocalNotepad.class,
        uses = AutoMapperConfiguration.StringListConverter.class)
public class MomoCloudNotepad {
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
        private final MomoCloudNotepad momoCloudNotepad = new MomoCloudNotepad();

        public Builder id(String id) {
            this.momoCloudNotepad.setId(id);
            return this;
        }

        public Builder status(Stats stats) {
            this.momoCloudNotepad.setStatus(stats);
            return this;
        }

        public Builder title(String title) {
            this.momoCloudNotepad.setTitle(title);
            return this;
        }

        public Builder brief(String brief) {
            this.momoCloudNotepad.setBrief(brief);
            return this;
        }

        public Builder content(String content) {
            this.momoCloudNotepad.setContent(content);
            return this;
        }

        public Builder tags(String... tags) {
            this.momoCloudNotepad.setTags(List.of(tags));
            return this;
        }

        public MomoCloudNotepad build() {
            return this.momoCloudNotepad;
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