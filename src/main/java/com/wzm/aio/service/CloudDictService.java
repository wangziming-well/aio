package com.wzm.aio.service;


import com.wzm.aio.domain.Word;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CloudDictService {

    private final Log logger = LogFactory.getLog(CloudDictService.class);

    private final DictionaryService  dictionaryService;
    private final MomoService momoService;

    public CloudDictService(DictionaryService dictionaryService, MomoService momoService){
        this.dictionaryService = dictionaryService;
        this.momoService = momoService;
    }

    private static final String WORD_SEP = " ";

    private static final char[] SEPARATOR = new char[]{';','；',',','，','.','。'};

    //本地词库同步到云端
    public boolean syncCloud(){
        List<Word> allWord = dictionaryService.findAllWord();
        String content = allWord.stream()
                .map(Word::getWord)
                .collect(Collectors.joining(WORD_SEP));

        return false;
    }

    public EndWordResult addWord(String... words){
        return addWord(List.of(words));
    }

    public EndWordResult addWord(List<String> words){
        List<String> all = words.stream()
                .map(String::toLowerCase)
                .distinct()
                .toList();
        ArrayList<String> success = new ArrayList<>();
        ArrayList<String> fail = new ArrayList<>();
        for (String word : all){
            boolean result = dictionaryService.addWord(word);
            if (result)
                success.add(word);
            else
                fail.add(word);
        }
        return new EndWordResult(all,success,fail);
    }

    public EndWordResult addWord(String text){
        return addWord(parse(text));
    }


    private boolean isSeparator(char c){
        for (char value : SEPARATOR) {
            if (value == c)
                return true;
        }
        return false;
    }

    private List<String> parse(String text){
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(isSeparator(chars[i]))
                chars[i] = WORD_SEP.charAt(0);
        }
        String s = new String(chars);
        return Arrays.stream(s.split(WORD_SEP))
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.toList());
    }


    @Getter
    @ToString
    public static class EndWordResult{
        private final List<String> allWords;
        private final List<String> successWords;
        private final List<String> failWords;

        private EndWordResult( List<String> allWords ,List<String> successWords, List<String> failWords) {
            this.successWords = Collections.unmodifiableList(successWords);
            this.failWords =Collections.unmodifiableList(failWords);
            this.allWords =Collections.unmodifiableList(allWords);
        }

        public int total(){
            return this.allWords.size();
        }

        public int successCount(){
            return this.successWords.size();
        }

        public int failCount(){
            return this.failWords.size();
        }

        public boolean allSuccess(){
            return successCount() == total();
        }

        public boolean hasFailed(){
            return !allSuccess();
        }

    }


}
