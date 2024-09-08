package com.wzm.aio.service;

import com.wzm.aio.domain.MomoLocalNotepad;
import com.wzm.aio.domain.NotepadDictPair;
import com.wzm.aio.domain.Word;
import com.wzm.aio.mapper.DictionaryMapper;
import com.wzm.aio.mapper.MomoNotepadMapper;
import com.wzm.aio.mapper.NotepadDictMapper;
import com.wzm.aio.properties.MomoProperties;
import com.wzm.aio.util.WordListParser;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MomoLocalService {


    private final MomoNotepadMapper momoNotepadMapper;
    private final DictionaryMapper dictionaryMapper;
    private final NotepadDictMapper notepadDictMapper;

    private final MomoProperties properties;

    public MomoLocalService(MomoNotepadMapper momoNotepadMapper
            , DictionaryMapper dictionaryMapper, NotepadDictMapper notepadDictMapper, MomoProperties properties) {

        this.momoNotepadMapper = momoNotepadMapper;
        this.dictionaryMapper = dictionaryMapper;
        this.notepadDictMapper = notepadDictMapper;
        this.properties = properties;
    }


    //清空本地notepad:截断表momo_notepad,和notepad_dictionary表
    public void clearNotepad() {
        this.momoNotepadMapper.truncateTable();
        this.notepadDictMapper.truncateTable();
        this.dictionaryMapper.truncateTable();
    }


    private List<String> getWords(int notepadId) {
        List<NotepadDictPair> notepadDictPairs = notepadDictMapper.selectByNotepadId(notepadId);
        if (ObjectUtils.isEmpty(notepadDictPairs))
            return Collections.emptyList();
        return notepadDictPairs.stream()
                .map(pair ->
                        dictionaryMapper.selectById(pair.getDictId()).getWord())
                .collect(Collectors.toList());
    }

    public MomoLocalNotepad getNotepad(int id) {
        MomoLocalNotepad result = momoNotepadMapper.selectById(id);
        result.setWords(getWords(result.getId()));
        return result;
    }

    public List<MomoLocalNotepad> getAllNotepads() {
        List<MomoLocalNotepad> momoLocalNotepads = momoNotepadMapper.selectAll();
        for (MomoLocalNotepad notepad :momoLocalNotepads){
            notepad.setWords(getWords(notepad.getId()));
        }
        return momoLocalNotepads;
    }

    private void checkCount() {
        int count = momoNotepadMapper.count();
        int maxCount = properties.getNotepad().getMaxCount();
        if (count >= maxCount)
            throw new IndexOutOfBoundsException("本地notepad的数量不能大于" + maxCount);
    }


    /**
     * 新增一个本地notepad，和该notepad中的words
     *
     * @param notepad 要添加的notepad
     * @return 新增成功返回ture
     * @throws IndexOutOfBoundsException 当notepad的数量不能大于7时
     */
    public void addNotepad(MomoLocalNotepad notepad) {
        checkCount();
        momoNotepadMapper.insertAndGetPrimaryKey(notepad);
        List<String> words = notepad.getWords();
        if (words == null)
            return;
        int notepadId = notepad.getId();
        for (String word : words) {
            Word localWord = dictionaryMapper.selectByWord(word);
            if (localWord == null) {
                localWord = new Word(word);
                dictionaryMapper.insertAndGetPrimaryKey(localWord);
            }
            int dictId = localWord.getId();
            notepadDictMapper.insert(notepadId, dictId);
        }
    }

    public void updateNotepad(MomoLocalNotepad localNotepad) {
        momoNotepadMapper.update(localNotepad);
    }


    /**
     * 按照本地ID删除本地notepad，和该notepad与dict的关联关系(notepad_dictionary)
     * @param id notepad的本地ID
     * @return 删除的notepad_dictionary条目个数，如果返回-1表示没有对应的notepad
     */
    public int deleteNotepad(int id) {
        int count = momoNotepadMapper.deleteById(id);
        if (count == 0)
            return -1;
        return notepadDictMapper.deleteByNotepadId(id);
    }

    public int deleteNotepad(String cloudId) {
        MomoLocalNotepad notepad = momoNotepadMapper.selectByCloudId(cloudId);
        if (notepad == null)
            return -1;
        return deleteNotepad(notepad.getId());
    }


    public EndWordResult addWords(String... words) {
        return addWords(List.of(words));
    }

    public EndWordResult addWords(List<String> words) {
        ArrayList<String> success = new ArrayList<>();
        ArrayList<String> fail = new ArrayList<>();
        for (String word : words) {
            boolean result = dictionaryMapper.insert(word);
            if (result)
                success.add(word);
            else
                fail.add(word);
        }
        return new EndWordResult(words, success, fail);
    }

    public EndWordResult addWords(String text) {
        return addWords(WordListParser.parse(text));
    }



    @Getter
    @ToString
    public static class EndWordResult {
        private final List<String> allWords;
        private final List<String> successWords;
        private final List<String> failWords;

        private EndWordResult(List<String> allWords, List<String> successWords, List<String> failWords) {
            this.successWords = Collections.unmodifiableList(successWords);
            this.failWords = Collections.unmodifiableList(failWords);
            this.allWords = Collections.unmodifiableList(allWords);
        }

        public int total() {
            return this.allWords.size();
        }

        public int successCount() {
            return this.successWords.size();
        }

        public int failCount() {
            return this.failWords.size();
        }

        public boolean allSuccess() {
            return successCount() == total();
        }

        public boolean hasFailed() {
            return !allSuccess();
        }

    }

}
