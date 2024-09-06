package com.wzm.aio.mapper;

import com.wzm.aio.domain.NotepadDictEntry;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotepadDictMapper {

    @Insert("INSERT INTO notepad_dictionary (notepad_id, dict_id) VALUES(#{notepadId}, #{dictId})")
    boolean insert(String notepadId,int dictId);

    @Select("SELECT notepad_id, dict_id FROM notepad_dictionary " +
            "WHERE notepad_id = #{notepadId} and dict_id = #{dictId}")
    NotepadDictEntry select(String notepadId, int dictId);

    @Select("SELECT notepad_id, dict_id FROM notepad_dictionary WHERE notepad_id = #{notepadId}")
    List<NotepadDictEntry> selectByNotepadId(String notepadId);

    @Select("SELECT notepad_id, dict_id FROM notepad_dictionary WHERE dict_id = #{dictId}")
    List<NotepadDictEntry>  selectByDictId(String dictId);

    @Delete("delete from notepad_dictionary where notepad_id = #{notepadId}")
    boolean deleteByNotepadId(String notepadId);
    @Delete("delete from notepad_dictionary where dict_id = #{dictId}")
    boolean deleteByDictId(String dictId);

    @Delete("delete from notepad_dictionary where notepad_id=#{notepadId} and dict_id = #{dictId}")
    boolean delete(String notepadId, String dictId);

}
