package com.wzm.aio.service;

import com.wzm.aio.api.MomoApi;
import com.wzm.aio.api.User;
import com.wzm.aio.util.JacksonUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MomoService {

    private final MomoApi api;

    private Map<String,String> userCookies ;

    private static final String USER_COOKIES_KEY = "userToken";


    public  MomoService(MomoApi api){
        this.api = api;
    }

    public boolean login(User user){
        ResponseEntity<String> result = api.login(JacksonUtil.toStringMap(user));
        if (result.getStatusCode().isError())
            return false;
        String body = result.getBody();
        Map<String, String> map = JacksonUtil.toMap(body);
        Assert.notNull(map,"空");
        String valid = map.get("valid");
        if (!"1".equals(valid))
            return false;
        return generateCookie(result.getHeaders());
    }

    public boolean post(String str){


        return false;
    }

    private boolean generateCookie(HttpHeaders headers){
        List<String> setCookies = headers.get(HttpHeaders.SET_COOKIE);
        if (setCookies == null)
            return false;
        for(String cookies : setCookies){
            if (cookies.startsWith(USER_COOKIES_KEY)){
                String value = cookies.split(";")[0].split("=")[1];

                HashMap<String, String> map = new HashMap<>();
                map.put(USER_COOKIES_KEY,value);
                userCookies = map;
                return true;
            }
        }
        return false;
    }


}
