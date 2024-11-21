package com.wzm.aio.api.momo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Phrase {
    private String id;
    @JsonProperty("voc_id")
    private String vocId;
    private String phrase;
    private String interpretation;
    private List<String> tags;
    private List<Highlight> highlight;
    private String status;
    @JsonProperty("created_time")

    private LocalDateTime createdTime;
    @JsonProperty("updated_time")
    private LocalDateTime updatedTime;
    private String origin;

    @Data
    public static class Highlight {
        private int start;
        private int end;
    }
}