package com.wzm.aio.service;

import com.wzm.aio.api.momo.MomoOpenApi;
import com.wzm.aio.api.momo.MomoResponse;
import com.wzm.aio.api.momo.model.Notepad;
import com.wzm.aio.api.momo.dto.NotepadsDTO;
import com.wzm.aio.api.momo.dto.NotepadDTO;
import com.wzm.aio.util.ThreadUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;

@Service
public class MomoCloudService {


    private final MomoOpenApi momoOpenApi;

    public MomoCloudService(MomoOpenApi momoOpenApi) {
        this.momoOpenApi = momoOpenApi;
    }

    private <T> void checkResult(MomoResponse<T> response) {
        Assert.notNull(response, "响应为空");
        boolean success = response.isSuccess();
        if (!success)
            throw new RuntimeException("响应失败:" + response);
    }

    private void checkRequired(Notepad notepad) {
        Assert.notNull(notepad.getTitle(), "title不能为空");
        Assert.notNull(notepad.getContent(), "content不能为空");
        Assert.notNull(notepad.getStatus(), "status不能为空");
        Assert.notNull(notepad.getBrief(), "brief不能为空");
        Assert.notNull(notepad.getTags(), "tags不能为空");
    }


    //获取当前用户的所有notepad,的完整信息
    public List<Notepad> getAllNotepads() {
        MomoResponse<NotepadsDTO> response = momoOpenApi.getAllNotepads();
        List<Notepad> notepads = response.getData().getNotepads();
        ArrayList<Notepad> result = new ArrayList<>();
        for (Notepad notepad : notepads) {
            ThreadUtils.sleep(1500);
            result.add(getNotepad(notepad.getId()));
        }
        return result;
    }

    public void deleteNotepad(String id) {
        MomoResponse<Void> response;
        try{
             response = momoOpenApi.deleteNotepad(id);
        } catch (WebClientResponseException e){
            if (e.getMessage().startsWith("503 Service Unavailable"))
                throw new RuntimeException("删除的notepad不存在",e);
            else
                throw e;
        }
        checkResult(response);
    }

    public void deleteAllNotepad() {
        MomoResponse<NotepadsDTO> response = momoOpenApi.getAllNotepads();
        List<Notepad> notepads = response.getData().getNotepads();
        for (Notepad notepad : notepads) {
            deleteNotepad(notepad.getId());
        }
    }

    public Notepad updateNotepad(Notepad notepad) {
        checkRequired(notepad);
        String id = notepad.getId();
        Assert.notNull(id, "id不能为空");
        MomoResponse<NotepadDTO> result = momoOpenApi.updateNotepad(id, new NotepadDTO(notepad));
        checkResult(result);
        return result.getData().getNotepad();
    }


    /**
     * 创建一个notepad
     *
     * @param notepad 要创建的notepad
     * @return 新创建的notepad的id，如果为空字符串，则表示创建失败
     */
    public Notepad createNotepad(Notepad notepad) {
        checkRequired(notepad);

        MomoResponse<NotepadDTO> result = momoOpenApi.createNotepad(new NotepadDTO(notepad));
        checkResult(result);
        return result.getData().getNotepad();
    }

    public Notepad createNotepad(String title, String brief, String content) {
        Notepad notepad = Notepad.builder().title(title).brief(brief).content(content).build();
        return createNotepad(notepad);
    }

    /**
     * 获取notepad
     * @param id notepad的云端id
     * @return 返回null表示云端notepad不存在
     */

    public Notepad getNotepad(String id) {
        MomoResponse<NotepadDTO> result;
        try {
            result = momoOpenApi.getNotepad(id);
        } catch (WebClientResponseException e) {
            if (e.getMessage().startsWith("503 Service Unavailable"))
                throw new RuntimeException("获取的notepad不存在",e);
            else
                throw e;
        }
        checkResult(result);
        return result.getData().getNotepad();
    }

}
