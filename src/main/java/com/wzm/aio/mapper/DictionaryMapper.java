package com.wzm.aio.mapper;

import com.wzm.aio.domain.Word;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DictionaryMapper {

    @Select("SELECT * FROM dictionary WHERE word = #{word}")
    Word find(@Param("word") String word);

    @Select("SELECT * FROM dictionary")
    List<Word> findAll();

    @Insert("insert into dictionary (word) values (#{word})")
    boolean addWord(String word);
    @Delete("delete from dictionary where word = #{word}")
    boolean deleteWord(String word);


}