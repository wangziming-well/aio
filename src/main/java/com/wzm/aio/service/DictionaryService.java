package com.wzm.aio.service;

import com.wzm.aio.domain.Word;
import com.wzm.aio.mapper.DictionaryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictionaryService {

    private final DictionaryMapper dictionaryMapper;

    public DictionaryService(DictionaryMapper dictionaryMapper){

        this.dictionaryMapper = dictionaryMapper;
    }

    public boolean addWord(String word){
        if (exist(word))
            return false;
        return dictionaryMapper.addWord(word);
    }

    public boolean deleteWord(String word){
        return dictionaryMapper.deleteWord(word);
    }

    public List<Word> findAllWord(){
        return dictionaryMapper.findAll();
    }

    public boolean exist(String word){
        return dictionaryMapper.find(word) != null;
    }


}
