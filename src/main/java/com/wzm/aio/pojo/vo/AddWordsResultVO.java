package com.wzm.aio.pojo.vo;

import com.wzm.aio.util.TextParser;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class AddWordsResultVO {
    @Getter
    @Setter
    public static class ResultEntry{

        public ResultEntry(String word){
            this.word = word;
        }

        private String word;
        private boolean newWords =false;
    }

    private List<ResultEntry> parsedWords;

}
