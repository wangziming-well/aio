package com.wzm.aio.service;


import com.wzm.aio.pojo.model.MomoCloudNotepad;
import com.wzm.aio.pojo.model.MomoLocalNotepad;
import com.wzm.aio.pojo.dto.MomoNotepadDTO;
import com.wzm.aio.util.ThreadUtils;
import com.wzm.aio.util.TextParser;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class MomoService {

    private final Log logger = LogFactory.getLog(MomoService.class);

    private final MomoLocalService localService;
    private final MomoCloudService cloudService;

    public MomoService(MomoLocalService localService, MomoCloudService cloudService) {
        this.localService = localService;
        this.cloudService = cloudService;
    }


    //云端词库同步到本地
    public void pull() {
        logger.info("同步云端词库到本地开始");
        List<MomoCloudNotepad> cloudNotepads = cloudService.getAllNotepads();
        localService.clearNotepad();
        logger.info("清空本地notepad");
        for (MomoCloudNotepad cloudNotepad : cloudNotepads) {
            MomoLocalNotepad local = cloudNotepad.toLocal();
            localService.addNotepad(local);
            logger.info("添加云端notepad[" + cloudNotepad.getId() + "]到本地，本地ID:" + local.getId());
        }
        logger.info("同步云端词库到本地结束");

    }

    //本地notepad同步到云端
    public void push() {
        logger.info("本地notepad同步云端开始");
        List<MomoLocalNotepad> localNotepads = localService.getAllNotepads();
        List<MomoCloudNotepad> cloudNotepads = cloudService.getAllNotepads();

        for (MomoCloudNotepad cloudNotepad : cloudNotepads) {
            String cloudId = cloudNotepad.getId();
            boolean shouldDelete = true;
            for (MomoLocalNotepad localNotepad : localNotepads) {
                if (Objects.equals(localNotepad.getCloudId(), cloudId)) {
                    shouldDelete = false;
                    break;
                }
            }
            if (shouldDelete) {
                logger.info("云端notepad[" + cloudId + "]在本地不存在，将被删除");
                cloudService.deleteNotepad(cloudId);
                ThreadUtils.sleep(1000);
            }
        }

        for (MomoLocalNotepad localNotepad : localNotepads) {
            String cloudId = localNotepad.getCloudId();
            //判断当前本地notepad是否在云端存在
            boolean cloudExist = false;
            for (MomoCloudNotepad cloudNotepad : cloudNotepads) {
                if (Objects.equals(cloudNotepad.getId(), cloudId)) {
                    cloudExist = true;
                    //如果云端存在当前notepad，且本地和云端存在不一致，则将更新云端notepad
                    if (isDiffer(cloudNotepad, localNotepad)) {
                        logger.info("云端notepad[" + cloudId + "]与本地不一致，将更新该云端notepad");
                        cloudService.updateNotepad(localNotepad.toCloud());
                    } else {
                        logger.info("云端notepad[" + cloudId + "]与本地一致，不需要更新");
                    }
                    break;
                }
            }
            //如果云端不存在当前notepad，将创建一个云端notepad
            if (!cloudExist) {
                logger.info("当前云端不存在notepad[" + cloudId + "]将新建该notepad到云端");

                cloudId = cloudService.createNotepad(localNotepad.toCloud()).getId();
                localNotepad.setCloudId(cloudId);
                localService.updateNotepad(localNotepad);
            }

        }
        logger.info("本地notepad同步云端完成");
    }

    private boolean isDiffer(MomoCloudNotepad cloud, MomoLocalNotepad local) {
        if (!Objects.equals(cloud.getTitle(), local.getTitle()))
            return true;
        if (!Objects.equals(cloud.getBrief(), local.getBrief()))
            return true;
        if (!Objects.equals(cloud.getStatus().toString(), local.getStatus()))
            return true;
        if (isDiffer(cloud.getContent(), local.getWords()))
            return true;
        if (isDiffer(local.getTags(), cloud.getTags()))
            return true;
        return false;
    }

    //内容不一致则返回true
    private boolean isDiffer(String text, List<String> words) {
        List<String> words1 = Arrays.stream(text.split(" ")).sorted().toList();
        if (words1.size() != words.size())
            return true;
        words.sort(String::compareTo);
        for (int i = 0; i < words.size(); i++) {
            if (!Objects.equals(words1.get(i), words.get(i)))
                return true;
        }
        return false;
    }

    public void addNotepad(MomoNotepadDTO notepadDTO) {
        MomoCloudNotepad cloudNotepad = notepadDTO.toCloud();
        cloudNotepad = cloudService.createNotepad(cloudNotepad);
        MomoLocalNotepad localNotepad = cloudNotepad.toLocal();
        localService.addNotepad(localNotepad);
    }

    private void checkLocalId(MomoNotepadDTO notepadDTO) {

    }

    public void updateNotepad(MomoNotepadDTO notepadDTO) {
        MomoLocalNotepad localNotepad = notepadDTO.toLocal();
        MomoCloudNotepad cloudNotepad = localNotepad.toCloud();
        MomoCloudNotepad updatedCloudNotepad = cloudService.updateNotepad(cloudNotepad);
        localService.updateNotepad(updatedCloudNotepad.toLocal());
    }
    //向notepad中添加单词，会同步到云端
    public AddWordsResult addWordsToNotepad(int localId, List<String> words) {
        MomoLocalService.AddWordsResult result = localService.addWordsToNotepad(localId, words);
        MomoLocalNotepad notepad = localService.getNotepad(localId);
        cloudService.updateNotepad(notepad.toCloud());
        return new AddWordsResult(result.getExistedWords(), result.getNewWords());
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

    @Transactional
    public void deleteNotepad(int localId) {
        MomoLocalNotepad notepad = localService.getNotepad(localId);
        String cloudId = notepad.getCloudId();
        cloudService.deleteNotepad(cloudId);
        localService.deleteNotepad(localId);
    }

    public MomoNotepadDTO getNotepad(int localId) {
        MomoLocalNotepad notepad = localService.getNotepad(localId);
        return notepad.toDTO();
    }


}
