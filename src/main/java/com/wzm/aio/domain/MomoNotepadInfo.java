package com.wzm.aio.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class MomoNotepadInfo {

    @JsonProperty("class")
    private String clazz;  // Renamed from "class" to "clazz" to avoid conflict with the Java keyword

    private String id;
    private String status;
    @JsonProperty("notepad_id")

    private String notepadId;  // CamelCase for "notepad_id"
    private int version;
    private String title;
    private String brief;
    private String content;
    private int like;
    private Map<String, String> tag;  // Assuming tag is a key-value map of Strings
    private String type;
    private String defs;
    private String list;
    private String order;
    @JsonProperty("is_private")
    private int isPrivate;  // CamelCase for "is_private"
    private Creator creator;
    @JsonProperty("like_user_count")
    private int likeUserCount;  // CamelCase for "like_user_count"
    private String revision;
    private String statistics;
    @JsonProperty("created_time")
    private String createdTime;  // CamelCase for "created_time"
    @JsonProperty("updated_time")
    private String updatedTime;  // CamelCase for "updated_time"
    @JsonProperty("deleted_time")
    private String deletedTime;  // CamelCase for "deleted_time"
    @JsonProperty("total_favorite_user")
    private int totalFavoriteUser;  // CamelCase for "total_favorite_user"
    @Data

    static public class Creator {

        private String id;
        private String uid;
        private String name;
        private int gender;
        private String avatar;
        private int level;
        @JsonProperty("max_voc_count")
        private int maxVocCount;  // CamelCase for "max_voc_count"
        @JsonProperty("learned_voc_count")
        private int learnedVocCount;  // CamelCase for "learned_voc_count"
        @JsonProperty("total_sign_days")
        private int totalSignDays;  // CamelCase for "total_sign_days"
        @JsonProperty("interpretation_created")
        private int interpretationCreated;  // CamelCase for "interpretation_created"
        @JsonProperty("interpretation_owned")
        private int interpretationOwned;  // CamelCase for "interpretation_owned"
        @JsonProperty("phrase_created")
        private int phraseCreated;  // CamelCase for "phrase_created"
        @JsonProperty("phrase_owned")
        private int phraseOwned;  // CamelCase for "phrase_owned"
        @JsonProperty("note_created")
        private int noteCreated;  // CamelCase for "note_created"
        @JsonProperty("note_owned")
        private int noteOwned;  // CamelCase for "note_owned"
        @JsonProperty("notepad_created")
        private int notepadCreated;  // CamelCase for "notepad_created"
        @JsonProperty("speech_created")
        private int speechCreated;  // CamelCase for "speech_created"
        @JsonProperty("created_time")
        private String createdTime;  // CamelCase for "created_time"
        @JsonProperty("class")
        private String clazz;  // Renamed from "class" to "clazz" to avoid conflict with the Java keyword
    }
}