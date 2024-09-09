package com.wzm.aio.service;

import com.wzm.aio.pojo.model.MomoLocalNotepad;
import com.wzm.aio.pojo.model.NotepadDictPair;
import com.wzm.aio.pojo.model.Word;
import com.wzm.aio.mapper.DictionaryMapper;
import com.wzm.aio.mapper.MomoNotepadMapper;
import com.wzm.aio.mapper.NotepadDictMapper;
import com.wzm.aio.properties.MomoProperties;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
    //获取本地notepad，包括wordList
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
    //更新本地notepad，没有更新notepad对应的wordList
    public void updateNotepad(MomoLocalNotepad localNotepad) {
        if (ObjectUtils.isEmpty(localNotepad.getId()))
            momoNotepadMapper.updateByCloudId(localNotepad);
        else if (ObjectUtils.isEmpty(localNotepad.getCloudId()))
            momoNotepadMapper.updateByLocalId(localNotepad);
        else
            throw new RuntimeException("id和cloudId都为空");
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

    /**
     * 向dictionary添加一个word，
     * @param word 要添加的Word，方法完成后，该Word对象的id将被赋值，为当前word在表中的id
     * @return 如果word已经存在，返回false，否则返回true。
     */

    public int addGlobalWord(String  word){
        Assert.hasText(word, "Word.word不能为空");
        Word exist = dictionaryMapper.selectByWord(word);
        if (exist != null){//如果要添加的单词已经存在
            return exist.getId();
        }
        Word toInsert = new Word(word);
        dictionaryMapper.insertAndGetPrimaryKey(toInsert);
        return toInsert.getId();
    }

    public AddWordsResult addWordsToNotepad(int localId,List<String> words){
        List<NotepadDictPair> notepadDictPairs = notepadDictMapper.selectByNotepadId(localId);
        int[] currWordIds = notepadDictPairs.stream().mapToInt(NotepadDictPair::getDictId).toArray();
        ArrayList<String> existedWords = new ArrayList<>();
        ArrayList<String> newWords = new ArrayList<>();
        for (String word: words){
            int wordId = addGlobalWord(word);
            int index = Arrays.binarySearch(currWordIds, wordId);
            if (index < 0){ //word不在notepad中，尝试添加关联关系
                notepadDictMapper.insert(localId,wordId);
                newWords.add(word);
            } else { //word在notepad中
                existedWords.add(word);
            }
        }
        return new AddWordsResult(existedWords,newWords);

    }

    @Getter
    @ToString
    public static class AddWordsResult {

        private final List<String> existedWords;
        private final List<String> newWords;

        public AddWordsResult(List<String> existedWords, List<String> newWords) {
            this.existedWords = Collections.unmodifiableList(existedWords);
            this.newWords = Collections.unmodifiableList(newWords);
        }
    }

}
