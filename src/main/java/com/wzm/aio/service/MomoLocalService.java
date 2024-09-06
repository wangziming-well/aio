package com.wzm.aio.service;

import com.wzm.aio.domain.MomoLocalNotepad;
import com.wzm.aio.domain.Word;
import com.wzm.aio.mapper.DictionaryMapper;
import com.wzm.aio.mapper.MomoNotepadMapper;
import com.wzm.aio.mapper.NotepadDictMapper;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MomoLocalService {


    private final MomoNotepadMapper momoNotepadMapper;
    private final DictionaryMapper dictionaryMapper;
    private final NotepadDictMapper notepadDictMapper;

    public MomoLocalService(MomoNotepadMapper momoNotepadMapper
    , DictionaryMapper dictionaryMapper, NotepadDictMapper notepadDictMapper){

        this.momoNotepadMapper = momoNotepadMapper;
        this.dictionaryMapper = dictionaryMapper;
        this.notepadDictMapper = notepadDictMapper;
    }


    public boolean insertNotepad(MomoLocalNotepad notepad){
        return momoNotepadMapper.insert(notepad);
    }

    public MomoLocalNotepad selectNotepadById(String id){
        return momoNotepadMapper.selectById(id);
    }

    public List<MomoLocalNotepad> selectAllNotepad(){
        return momoNotepadMapper.selectAll();
    }

    public boolean insertWord(String word){
        return dictionaryMapper.insert(word);
    }

    public boolean deleteWord(String word){
        return dictionaryMapper.deleteByWord(word);
    }
    public boolean deleteWordById(String id){
        return dictionaryMapper.deleteById(id);
    }
    public Word selectWordById(String id){
        return dictionaryMapper.selectById(id);
    }

    public Word selectWord(String word){
        return dictionaryMapper.selectByWord(word);
    }

    public List<Word> selectAllWords(){
        return dictionaryMapper.selectAll();
    }


    public EndWordResult addWords(String... words){
        return addWords(List.of(words));
    }

    public EndWordResult addWords(List<String> words){
        List<String> all = words.stream()
                .map(String::toLowerCase)
                .distinct()
                .toList();
        ArrayList<String> success = new ArrayList<>();
        ArrayList<String> fail = new ArrayList<>();
        for (String word : all){
            boolean result = insertWord(word);
            if (result)
                success.add(word);
            else
                fail.add(word);
        }
        return new EndWordResult(all,success,fail);
    }

    public EndWordResult addWords(String text){
        return addWords(parse(text));
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

    private static final String WORD_SEP = " ";

    private static final char[] SEPARATOR = new char[]{';','；',',','，','.','。'};
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
