package com.wzm.aio.mapper;

import com.wzm.aio.pojo.model.MomoLocalNotepad;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MomoNotepadMapper {
    List<MomoLocalNotepad> selectAll();
    MomoLocalNotepad selectById(int id);

    MomoLocalNotepad selectByTitle(String title);

    MomoLocalNotepad selectByCloudId(String cloudId);

    int updateByLocalId(MomoLocalNotepad localNotepad);

    int updateByCloudId(MomoLocalNotepad localNotepad);

    int insert(MomoLocalNotepad notepad);

    int insertAndGetPrimaryKey(MomoLocalNotepad notepad);

    @Update("truncate table momo_notepad")
    void truncateTable();

    @Select("select count(*) from momo_notepad")
    int count();
    @Delete("delete from momo_notepad where id = #{id}")
    int deleteById(int id);

}
