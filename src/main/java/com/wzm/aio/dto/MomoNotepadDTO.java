package com.wzm.aio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class MomoNotepadDTO {

    private int id;
    private String cloudId;
    private String type;
    private String status;
    private String title;
    private String brief;
    private String tags;
    @JsonProperty("created_time")
    private OffsetDateTime createdTime;
    @JsonProperty("updated_time")
    private OffsetDateTime updatedTime;
    List<String> words;


}
