package com.wzm.aio.mapper;

import com.wzm.aio.domain.Word;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DictionaryMapper {

    @Select("SELECT * FROM dictionary WHERE id = #{id}")
    Word selectById(@Param("id") String id);

    @Select("SELECT * FROM dictionary WHERE word = #{word}")
    Word selectByWord(@Param("word") String word);

    @Select("SELECT * FROM dictionary")
    List<Word> selectAll();

    @Insert("insert into dictionary (word) values (#{word})")
    boolean insert(String word);
    @Delete("delete from dictionary where word = #{word}")
    boolean deleteByWord(String word);

    @Delete("delete from dictionary where id = #{id}")
    boolean deleteById(String id);



}