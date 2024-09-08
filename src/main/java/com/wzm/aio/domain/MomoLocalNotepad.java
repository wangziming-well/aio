package com.wzm.aio.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wzm.aio.api.entity.MomoCloudNotepad;
import com.wzm.aio.config.AutoMapperConfiguration;
import com.wzm.aio.util.WordListParser;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@AutoMapper(target = MomoCloudNotepad.class,
        uses = AutoMapperConfiguration.StringListConverter.class)
public class MomoLocalNotepad {

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

    public MomoCloudNotepad toCloud() {
        MomoCloudNotepad cloud = new MomoCloudNotepad();

        try {
            BeanUtils.copyProperties(cloud,this);
        } catch (IllegalAccessException | InvocationTargetException |IllegalArgumentException e) {

        }
        cloud.setId(this.cloudId);
        cloud.setContent(WordListParser.join(this.words));
        cloud.setTags(WordListParser.parse(this.tags));
        cloud.setType(MomoCloudNotepad.Type.valueOf(this.type));
        cloud.setStatus(MomoCloudNotepad.Stats.valueOf(this.status));
        return cloud;
    }
}
