package com.wzm.aio.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wzm.aio.api.MomoApi;
import com.wzm.aio.domain.*;
import com.wzm.aio.util.JacksonUtils;
import com.wzm.aio.util.MomoCookiesHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        MomoCookies cookies = MomoCookies.generate(result.getHeaders());
        logger.info("登录成功，当前用户cookies为:" + cookies);
        MomoCookiesHolder.INSTANCE.set(cookies);
        return true;
    }


    //用户登出
    public void logout(){
        logger.info("用户退出登录");
        momoApi.logout();
        MomoCookiesHolder.INSTANCE.clear();
        logger.info("用户登出成功");
    }

    //获取当前用户的所有notepad
    public List<MomoNotepadInfo> getAllNotepad(){
        String userToken = MomoCookiesHolder.INSTANCE.get().getUserToken();
        ResponseEntity<String> result = momoApi.searchNotepad(userToken,new MomoNotepadRequestBody());
        if (!requestSuccess(result))
            return Collections.emptyList();
        Map<String, Object> map = JacksonUtils.parseToMap(result.getBody());
        Object notepads = map.get("notepad");
        List<MomoNotepadInfo> momoNotepadInfoList = JacksonUtils.convertTo(notepads, new TypeReference<>() {
        });
        logger.info("获取momoNotepadInfoList" + momoNotepadInfoList);
        return momoNotepadInfoList;
    }
    //保存notepad，如果notepad的id为0，意为新增一个notepad
    public boolean saveNotepad(MomoNotepad notepad){
        logger.info("保存notepad:" + notepad);
        Map<String, String> notepadMap = JacksonUtils.convertToStrMap(notepad);
        ResponseEntity<String> result = momoApi.saveNotepad(notepadMap);
        System.out.println(result.getStatusCode());
        return requestSuccess(result);
    }

    public boolean deleteNotepad(MomoNotepad notepad){
        String id = notepad.getId();
        logger.info("删除notepad:" + id);
        ResponseEntity<String> result = momoApi.deleteNotepad( id);
        return requestSuccess(result);
    }


    private boolean requestSuccess(ResponseEntity<String> result){
        HttpStatusCode statusCode = result.getStatusCode();
        if (statusCode.is3xxRedirection()){
            logger.info("请求失败，请求码:" + statusCode + ",用户cookies可能失效");
            return false;
        }

        if (statusCode.isError()){
            logger.info("请求失败，请求码:" + statusCode );
            return false;
        }
        String body = result.getBody();
        if (!StringUtils.hasText(body)){
            logger.info("请求失败，请求体为空");
            return false;
        }
        Map<String, Object> map = JacksonUtils.parseToMap(body);
        Object valid = map.get("valid");
        if(valid == null || !"1".equals(valid.toString()) && Boolean.FALSE.equals(valid)){
            logger.info("请求失败，响应为:" + result.getBody());
            return false;
        }
        return  true;
    }

}
