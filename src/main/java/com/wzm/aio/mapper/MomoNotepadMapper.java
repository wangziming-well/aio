package com.wzm.aio.mapper;

import com.wzm.aio.domain.MomoLocalNotepad;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MomoNotepadMapper {

    MomoLocalNotepad selectById(String id);
    boolean insert(MomoLocalNotepad notepad);

    List<MomoLocalNotepad> selectAll();

}
