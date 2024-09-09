package com.wzm.aio.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wzm.aio.pojo.model.MomoCloudNotepad;
import com.wzm.aio.config.AutoMapperConfiguration;
import com.wzm.aio.pojo.model.MomoLocalNotepad;
import com.wzm.aio.util.BeanUtils;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data

@AutoMappers({@AutoMapper(target = MomoCloudNotepad.class,
        uses = AutoMapperConfiguration.StringListConverter.class),
        @AutoMapper(target = MomoLocalNotepad.class)})
public class MomoNotepadDTO {
    @AutoMapping(targetClass =MomoCloudNotepad.class ,target = "id",ignore = true)
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
    @AutoMapping(targetClass = MomoCloudNotepad.class,target = "content")
    List<String> words;

    public MomoLocalNotepad toLocal(){
        return BeanUtils.convert(this, MomoLocalNotepad.class);
    }

    public MomoCloudNotepad toCloud(){
        MomoCloudNotepad convert = BeanUtils.convert(this, MomoCloudNotepad.class);
        convert.setId(this.cloudId);
        return convert;
    }

}
