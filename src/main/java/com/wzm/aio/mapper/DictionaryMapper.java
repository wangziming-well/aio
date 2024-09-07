package com.wzm.aio.mapper;

import com.wzm.aio.domain.Word;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DictionaryMapper {

    @Select("SELECT * FROM dictionary WHERE id = #{id}")
    Word selectById(@Param("id") int id);

    @Select("SELECT * FROM dictionary WHERE word = #{word}")
    Word selectByWord(@Param("word") String word);

    @Select("SELECT * FROM dictionary")
    List<Word> selectAll();

    @Insert("insert into dictionary (word) values (#{word})")
    boolean insert(String word);

    @Insert("insert into dictionary (word) values (#{word})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    boolean insertAndGetPrimaryKey(Word word);
    @Delete("delete from dictionary where word = #{word}")
    boolean deleteByWord(String word);

    @Delete("delete from dictionary where id = #{id}")
    boolean deleteById(int id);

    @Update("truncate table dictionary")
    void truncateTable();

}