package com.wzm.aio.service;

import com.wzm.aio.pojo.model.MomoLocalNotepad;
import com.wzm.aio.pojo.model.NotepadDictPair;
import com.wzm.aio.pojo.model.Word;
import com.wzm.aio.mapper.DictionaryMapper;
import com.wzm.aio.mapper.MomoNotepadMapper;
import com.wzm.aio.mapper.NotepadDictMapper;
import com.wzm.aio.properties.OpenApiProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class MomoLocalService {


    private final MomoNotepadMapper momoNotepadMapper;
    private final DictionaryMapper dictionaryMapper;
    private final NotepadDictMapper notepadDictMapper;

    private final OpenApiProperties properties;

    public MomoLocalService(MomoNotepadMapper momoNotepadMapper
            , DictionaryMapper dictionaryMapper, NotepadDictMapper notepadDictMapper, OpenApiProperties properties) {

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

    public int getNotepadId(String title){
        MomoLocalNotepad momoLocalNotepad = momoNotepadMapper.selectByTitle(title);
        return momoLocalNotepad.getId();
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
        int maxCount = properties.getMomo().getNotepadMaxCount();
        if (count >= maxCount)
            throw new IndexOutOfBoundsException("本地notepad的数量不能大于" + maxCount);
    }


    /**
     * 新增一个本地notepad，和该notepad中的words
     *
     * @param notepad 要添加的notepad
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
            linkWordToNotepad(notepadId, dictId);

        }
    }
    //更新本地notepad，不更新notepad对应的wordList
    public void updateNotepad(MomoLocalNotepad localNotepad) {
        if (!ObjectUtils.isEmpty(localNotepad.getCloudId()))
            momoNotepadMapper.updateByCloudId(localNotepad);
        else if (!ObjectUtils.isEmpty(localNotepad.getId()))
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

    /**
     * 根据localId向本地notepad中添加words
     * @param localId 本地notepad id
     * @param words 要添加的word列表
     * @return 返回一个Map，键为添加的单词，值为布尔值，true表示添加成功，false表示添加失败，一般是因为notepad中已经有了该单词
     */

    public Map<String,Boolean> addWordsToNotepad(int localId, List<String> words){
        List<NotepadDictPair> notepadDictPairs = notepadDictMapper.selectByNotepadId(localId);
        int[] currWordIds = notepadDictPairs.stream().mapToInt(NotepadDictPair::getDictId).toArray();
        HashMap<String, Boolean> result = new HashMap<>();
        for (String word: words){
            int wordId = addGlobalWord(word);
            int index = Arrays.binarySearch(currWordIds, wordId);
            if (index < 0){ //word不在notepad中，尝试添加关联关系
                linkWordToNotepad(localId,wordId);
                result.put(word,true);
            } else { //word在notepad中
                result.put(word,false);
            }
        }
        return result;

    }

    public void linkWordToNotepad(int localNotepadId,int wordId){
        NotepadDictPair select = notepadDictMapper.select(localNotepadId, wordId);
        if (select != null)
            return;
        notepadDictMapper.insert(localNotepadId,wordId);
    }
}
