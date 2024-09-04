package com.wzm.aio.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wzm.aio.api.MomoApi;
import com.wzm.aio.domain.*;
import com.wzm.aio.util.JacksonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MomoService {

    private static final Log logger = LogFactory.getLog(MomoService.class);

    private final MomoApi momoApi;


    public  MomoService(MomoApi momoApi){
        this.momoApi = momoApi;
    }

    /**
     * 用户登录，登录成功后会缓存登录信息到momoCookies
     * @param momoUser 用户信息
     * @return 登录成功返回true，否则返回false
     */

    public boolean login(MomoUser momoUser){
        logger.info("用户登录:" + momoUser);
        ResponseEntity<String> result = momoApi.login(JacksonUtils.toStringMap(momoUser));
        if (!requestSuccess(result))
            return false;
        return generateCookie(result.getHeaders());
    }


    //用户登出
    public void logout(){
        momoApi.logout();
        MomoCookies.INSTANCE.clear();
        logger.info("用户登出成功");
    }

    //获取当前用户的所有notepad
    public List<MomoNotepadInfo> getAllNotepad(){
        String userToken = MomoCookies.INSTANCE.getCookiesMap().get(MomoCookies.USER_KEY).get(0);
        ResponseEntity<String> result = momoApi.searchNotepad(userToken,new MomoNotepadRequestBody());
        if (result.getStatusCode().isError()){
            logger.info("获取notepadInfoList失败,响应码为:" + result.getStatusCode());
            return Collections.emptyList();
        }
        Map<String, Object> map = JacksonUtils.parseToMap(result.getBody());
        Object notepads = map.get("notepad");
        return JacksonUtils.convertTo(notepads, new TypeReference<>() {});
    }
    //保存notepad，如果notepad的id为0，意为新增一个notepad
    public boolean saveNotepad(MomoNotepad notepad){
        logger.info("保存notepad:" + notepad);
        Map<String, String> notepadMap = JacksonUtils.convertToStrMap(notepad);
        ResponseEntity<String> result = momoApi.saveNotepad(notepadMap);
        return requestSuccess(result);
    }

    public boolean deleteNotepad(MomoNotepad notepad){
        String id = notepad.getId();
        logger.info("删除notepad:" + id);
        ResponseEntity<String> result = momoApi.deleteNotepad( id);
        return requestSuccess(result);
    }


    private boolean generateCookie(HttpHeaders headers){
        List<String> setCookies = headers.get(HttpHeaders.SET_COOKIE);
        if (setCookies == null)
            return false;
        MomoCookies.INSTANCE.fill(setCookies);
        logger.info("登录成功，当前用户Cookies为:" + MomoCookies.INSTANCE.getCookiesMap());
        return true;
    }

    private boolean requestSuccess(ResponseEntity<String> result){
        if (result.getStatusCode().isError())
            return false;
        if (result.getBody()== null)
            return false;
        Map<String, Object> map = JacksonUtils.parseToMap(result.getBody());
        Object valid = map.get("valid");
        return valid != null && ("1".equals(valid.toString()) || Boolean.TRUE.equals(valid) );
    }

}
