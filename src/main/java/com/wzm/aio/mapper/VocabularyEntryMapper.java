package com.wzm.aio.mapper;

import com.wzm.aio.pojo.model.VocabularyEntry;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VocabularyEntryMapper {

    @Insert("INSERT INTO notepad_dictionary (notepad_id, dict_id) VALUES(#{notepadId}, #{dictId})")
    boolean insert(int notepadId,int dictId);
    @Results({@Result(property = "notepadId", column = "notepad_id"),
            @Result(property = "dictId", column = "dict_id")})
    @Select("SELECT notepad_id, dict_id FROM notepad_dictionary " +
            "WHERE notepad_id = #{notepadId} and dict_id = #{dictId}")
    VocabularyEntry select(int notepadId, int dictId);

    @Results({@Result(property = "notepadId", column = "notepad_id"),
            @Result(property = "dictId", column = "dict_id")})
    @Select("SELECT notepad_id, dict_id FROM notepad_dictionary WHERE notepad_id = #{notepadId}")
    List<VocabularyEntry> selectByNotepadId(int notepadId);
    @Results({@Result(property = "notepadId", column = "notepad_id"),
            @Result(property = "dictId", column = "dict_id")})
    @Select("SELECT notepad_id, dict_id FROM notepad_dictionary WHERE dict_id = #{dictId}")
    List<VocabularyEntry>  selectByDictId(int dictId);

    @Delete("delete from notepad_dictionary where notepad_id = #{notepadId}")
    int deleteByNotepadId(int notepadId);
    @Delete("delete from notepad_dictionary where dict_id = #{dictId}")
    int deleteByDictId(int dictId);

    @Delete("delete from notepad_dictionary where notepad_id=#{notepadId} and dict_id = #{dictId}")
    boolean delete(String notepadId, String dictId);

    @Update("truncate table notepad_dictionary")
    void truncateTable();
}