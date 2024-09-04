package com.wzm.aio.api;

import com.wzm.aio.domain.MomoNotepadRequestBody;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.Map;

/**
 * 墨墨背单词HTTP API
 */
@HttpExchange
public interface MomoApi {

    /**
     * 用户登录
     * @param user 入参为
     * @return 响应体格式为{"valid":1}(登录成功) 或者 {"valid":0,"error":"invalidLogin"}(登录失败)
     */
    @PostExchange(value = "/auth/login" ,contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<String> login(@RequestParam Map<String, String> user);

    /**
     * 用户登出
     */
    @GetExchange(value = "/auth/logout")
    void logout();

    /**
     * 获取当前用户所有的notepad信息
     * @param token 用户登录cookies中的token
     * @param body 请求体MomoNotepadRequestBody
     * @return 响应notepad列表
     */
    @PostExchange(value = "v3/api/notepad/search",contentType = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> searchNotepad(@RequestParam String token, @RequestBody MomoNotepadRequestBody body);

    /**
     * 保存或新增notepad，当id为0时为新增
     * @param body notepad信息
     * @return 成功返回响应 {"valid":1}
     */

    @PostExchange(value = "/notepad/save",contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<String> saveNotepad(@RequestParam Map<String, String> body);

    /**
     * 删除指定id的notepad
     * @param id notepad的id
     * @return 成功删除返回响应
     */

    @GetExchange(value = "/notepad/delete/{id}")
    ResponseEntity<String> deleteNotepad(@PathVariable String id);


}
