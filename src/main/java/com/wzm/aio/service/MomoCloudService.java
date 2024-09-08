package com.wzm.aio.service;

import com.wzm.aio.api.MomoOpenApi;
import com.wzm.aio.api.MomoResponse;
import com.wzm.aio.api.entity.MomoCloudNotepad;
import com.wzm.aio.api.entity.NotepadList;
import com.wzm.aio.api.entity.OneNotepad;
import com.wzm.aio.properties.MomoProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;

@Service
public class MomoCloudService {

    private static final Log logger = LogFactory.getLog(MomoCloudService.class);

    private final MomoOpenApi momoOpenApi;
    private final MomoProperties properties;

    public MomoCloudService(MomoOpenApi momoOpenApi, MomoProperties properties) {
        this.momoOpenApi = momoOpenApi;
        this.properties = properties;
    }


    private <T> T getData(ResponseEntity<MomoResponse<T>> response) {
        MomoResponse<T> body = response.getBody();
        Assert.notNull(body, "请求体为空");
        return body.getData();
    }

    private <T> boolean isSuccess(ResponseEntity<MomoResponse<T>> response) {
        MomoResponse<T> body = response.getBody();
        Assert.notNull(body, "请求体为空");
        return body.isSuccess();
    }

    private void checkRequired(MomoCloudNotepad momoCloudNotepad) {
        Assert.notNull(momoCloudNotepad.getTitle(), "title不能为空");
        Assert.notNull(momoCloudNotepad.getContent(), "content不能为空");
        Assert.notNull(momoCloudNotepad.getStatus(), "status不能为空");
        Assert.notNull(momoCloudNotepad.getBrief(), "brief不能为空");
        Assert.notNull(momoCloudNotepad.getTags(), "tags不能为空");
    }


    //获取当前用户的所有notepad
    public List<MomoCloudNotepad> getAllNotepads() {
        ResponseEntity<MomoResponse<NotepadList>> response = momoOpenApi.getAllNotepads();
        List<MomoCloudNotepad> notepads = getData(response).getNotepads();
        ArrayList<MomoCloudNotepad> result = new ArrayList<>();
        for (MomoCloudNotepad notepad : notepads) {
            result.add(getNotepad(notepad.getId()));
        }
        return result;
    }

    public boolean deleteNotepad(String id) {
        ResponseEntity<MomoResponse<Void>> response = momoOpenApi.deleteNotepad(id);
        return isSuccess(response);
    }

    public void deleteAllNotepad() {
        ResponseEntity<MomoResponse<NotepadList>> response = momoOpenApi.getAllNotepads();
        List<MomoCloudNotepad> notepads = getData(response).getNotepads();
        for (MomoCloudNotepad notepad : notepads) {
            deleteNotepad(notepad.getId());
        }
    }

    public boolean updateNotepad(MomoCloudNotepad momoCloudNotepad) {
        checkRequired(momoCloudNotepad);
        String id = momoCloudNotepad.getId();
        Assert.notNull(id, "id不能为空");
        ResponseEntity<MomoResponse<OneNotepad>> result = momoOpenApi.updateNotepad(id, new OneNotepad(momoCloudNotepad));
        return isSuccess(result);
    }


    /**
     * 创建一个notepad
     *
     * @param momoCloudNotepad 要创建的notepad
     * @return 新创建的notepad的id，如果为空字符串，则表示创建失败
     */
    public String createNotepad(MomoCloudNotepad momoCloudNotepad) {
        checkRequired(momoCloudNotepad);

        ResponseEntity<MomoResponse<OneNotepad>> result = momoOpenApi.createNotepad(new OneNotepad(momoCloudNotepad));
        if (!isSuccess(result))
            return "";
        return getData(result).getNotepad().getId();
    }

    public String createNotepad(String title, String brief, String content) {
        MomoCloudNotepad momoCloudNotepad = MomoCloudNotepad.builder().title(title).brief(brief).content(content).build();
        return createNotepad(momoCloudNotepad);
    }

    /**
     * 获取notepad
     * @param id notepad的云端id
     * @return 返回null表示云端notepad不存在
     */

    public MomoCloudNotepad getNotepad(String id) {
        ResponseEntity<MomoResponse<OneNotepad>> result;
        try {
            result = momoOpenApi.getNotepad(id);
        } catch (WebClientResponseException e) {
            if (e.getMessage().startsWith("503 Service Unavailable"))
                return null;
            else
                throw e;
        }
        if (!isSuccess(result))
            return null;
        OneNotepad data = getData(result);
        return data.getNotepad();
    }

}
