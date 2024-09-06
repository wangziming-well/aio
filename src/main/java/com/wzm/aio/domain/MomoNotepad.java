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



}
