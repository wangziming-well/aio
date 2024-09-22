package com.wzm.aio.service;

import com.wzm.aio.api.frdic.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrDicService {

    private final FrDicOpenApi api;


    public FrDicService(FrDicOpenApi api) {
        this.api = api;
    }

    public List<FrWordsBook> getAllWordsBook(String language) {
        FrDicResponse<List<FrWordsBook>> response = api.getAllWordsBooks(language);
        return response.getData();
    }

    public List<FrWordsBook> getAllWordsBook() {
        return getAllWordsBook(SupportLanguage.EN);
    }

    public List<FrWord> getAllWords(String wordsBookId, String language) {
        FrDicResponse<List<FrWord>> response = api.getWords(wordsBookId, language, 0, Integer.MAX_VALUE);
        return response.getData();
    }

    public List<FrWord> getAllWords(String wordsBookId) {
        return getAllWords(wordsBookId,SupportLanguage.EN);
    }

    public String getWordsBookId(String wordsBookName){
        List<FrWordsBook> allFrWordsBook = getAllWordsBook();
        for (FrWordsBook frWordsBook : allFrWordsBook){
            if (frWordsBook.getName().equals(wordsBookName))
                return frWordsBook.getId();
        }
        throw new RuntimeException("没有该名称的单词本");
    }


}