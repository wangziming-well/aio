package com.wzm.aio.pojo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wzm.aio.api.momo.model.Notepad;
import com.wzm.aio.config.AutoMapperConfiguration;
import com.wzm.aio.pojo.dto.MomoNotepadDTO;
import com.wzm.aio.util.BeanUtils;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AutoMappers({@AutoMapper(target = Notepad.class,
        uses = AutoMapperConfiguration.StringListConverter.class),
        @AutoMapper(target = MomoNotepadDTO.class)})
public class MomoLocalNotepad {
    @AutoMapping(target = "id",ignore = true)
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
    @AutoMapping(targetClass = Notepad.class,target = "content")
    List<String> words;


    public Notepad toCloud(){
        Notepad convert = BeanUtils.convert(this, Notepad.class);
        convert.setId(this.cloudId);
        return convert;
    }

    public MomoNotepadDTO toDTO(){
        return BeanUtils.convert(this,MomoNotepadDTO.class);
    }
}
