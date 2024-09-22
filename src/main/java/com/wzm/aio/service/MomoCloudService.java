package com.wzm.aio.service;

import com.wzm.aio.api.momo.MomoOpenApi;
import com.wzm.aio.api.momo.MomoResponse;
import com.wzm.aio.pojo.model.MomoCloudNotepad;
import com.wzm.aio.api.momo.NotepadList;
import com.wzm.aio.api.momo.OneNotepad;
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

    private void checkRequired(MomoCloudNotepad momoCloudNotepad) {
        Assert.notNull(momoCloudNotepad.getTitle(), "title不能为空");
        Assert.notNull(momoCloudNotepad.getContent(), "content不能为空");
        Assert.notNull(momoCloudNotepad.getStatus(), "status不能为空");
        Assert.notNull(momoCloudNotepad.getBrief(), "brief不能为空");
        Assert.notNull(momoCloudNotepad.getTags(), "tags不能为空");
    }


    //获取当前用户的所有notepad,的完整信息
    public List<MomoCloudNotepad> getAllNotepads() {
        MomoResponse<NotepadList> response = momoOpenApi.getAllNotepads();
        List<MomoCloudNotepad> notepads = response.getData().getNotepads();
        ArrayList<MomoCloudNotepad> result = new ArrayList<>();
        for (MomoCloudNotepad notepad : notepads) {
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
        MomoResponse<NotepadList> response = momoOpenApi.getAllNotepads();
        List<MomoCloudNotepad> notepads = response.getData().getNotepads();
        for (MomoCloudNotepad notepad : notepads) {
            deleteNotepad(notepad.getId());
        }
    }

    public MomoCloudNotepad updateNotepad(MomoCloudNotepad momoCloudNotepad) {
        checkRequired(momoCloudNotepad);
        String id = momoCloudNotepad.getId();
        Assert.notNull(id, "id不能为空");
        MomoResponse<OneNotepad> result = momoOpenApi.updateNotepad(id, new OneNotepad(momoCloudNotepad));
        checkResult(result);
        return result.getData().getNotepad();
    }


    /**
     * 创建一个notepad
     *
     * @param momoCloudNotepad 要创建的notepad
     * @return 新创建的notepad的id，如果为空字符串，则表示创建失败
     */
    public MomoCloudNotepad createNotepad(MomoCloudNotepad momoCloudNotepad) {
        checkRequired(momoCloudNotepad);

        MomoResponse<OneNotepad> result = momoOpenApi.createNotepad(new OneNotepad(momoCloudNotepad));
        checkResult(result);
        return result.getData().getNotepad();
    }

    public MomoCloudNotepad createNotepad(String title, String brief, String content) {
        MomoCloudNotepad momoCloudNotepad = MomoCloudNotepad.builder().title(title).brief(brief).content(content).build();
        return createNotepad(momoCloudNotepad);
    }

    /**
     * 获取notepad
     * @param id notepad的云端id
     * @return 返回null表示云端notepad不存在
     */

    public MomoCloudNotepad getNotepad(String id) {
        MomoResponse<OneNotepad> result;
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
