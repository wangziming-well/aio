package com.wzm.aio.mapper;

import com.wzm.aio.domain.MomoLocalNotepad;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MomoNotepadMapper {
    List<MomoLocalNotepad> selectAll();
    MomoLocalNotepad selectById(int id);

    MomoLocalNotepad selectByCloudId(String cloudId);

    void update(MomoLocalNotepad localNotepad);

    boolean insert(MomoLocalNotepad notepad);

    boolean insertAndGetPrimaryKey(MomoLocalNotepad notepad);

    @Update("truncate table momo_notepad")
    void truncateTable();

    @Select("select count(*) from momo_notepad")
    int count();
    @Delete("delete from momo_notepad where id = #{id}")
    boolean deleteById(int id);

}
