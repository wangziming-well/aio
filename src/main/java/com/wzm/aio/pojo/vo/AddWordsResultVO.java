package com.wzm.aio.pojo.vo;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class AddWordsResultVO {
    @Setter
    @Getter
    @AllArgsConstructor
    public static class ResultEntry{


        private String word;
        private boolean flag;
    }

    private List<ResultEntry> parsedWords = new ArrayList<>();

    public void addEntry(String word,boolean flag){
        parsedWords.add(new ResultEntry(word,flag));
    }

    public static AddWordsResultVO fromMap(Map<String,Boolean> maps){
        Set<Map.Entry<String, Boolean>> entries = maps.entrySet();
        AddWordsResultVO vo = new AddWordsResultVO();
        for (Map.Entry<String,Boolean> entry : entries){
            vo.addEntry(entry.getKey(),entry.getValue());
        }
        return vo;
    }


}
