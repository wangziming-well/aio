package com.wzm.aio.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wzm.aio.domain.MomoNotepad;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class Notepad {

    public enum Stats{
        PUBLISHED,UNPUBLISHED,DELETE
    }

    public enum Type{
        FAVORITE,NOTEPAD
    }

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

    @Data
    public static class NotepadItem {
        private String type;
        private String chapter;
        private String word;
    }

}