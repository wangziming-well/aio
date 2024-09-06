package com.wzm.aio.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wzm.aio.domain.MomoLocalNotepad;
import com.wzm.aio.util.JacksonUtils;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MomoCloudNotepad {


    private String id;
    private Type type;
    private Stats status = Stats.PUBLISHED;
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
        MomoLocalNotepad local = new MomoLocalNotepad();

        try {
            BeanUtils.copyProperties(local,this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        local.setTags(String.join(" ", this.tags));
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

    public enum Stats {
        PUBLISHED, UNPUBLISHED, DELETE
    }

    public enum Type {
        FAVORITE, NOTEPAD
    }


}