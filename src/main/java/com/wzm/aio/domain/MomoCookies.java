package com.wzm.aio.domain;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.List;


@Getter
@ToString
public class MomoCookies {


    public MomoCookies(String userToken, String phpsessid, int maxAge) {
        this.userToken = userToken;
        this.phpsessid = phpsessid;
        this.maxAge = maxAge;
        this.setTime = System.currentTimeMillis();
    }

    public static final String USER_KEY = "userToken";
    public static final String PHPSESSID_KEY = "PHPSESSID";

    private final String userToken;
    private final String phpsessid;
    private final int maxAge; // s
    private final long setTime; //ms

    public MultiValueMap<String, String> toMap() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put(USER_KEY, List.of(this.userToken));
        map.put(PHPSESSID_KEY, List.of(this.phpsessid));
        return map;
    }

    //判断当前cookies是否过期
    public boolean isExpire() {
        return System.currentTimeMillis() - this.setTime > this.maxAge * 1000L;

    }

    //根据HTTP header 解析获取MomoCookies实例，解析失败将抛出异常
    public static MomoCookies generate(HttpHeaders headers) {
        List<String> setCookies = headers.get(HttpHeaders.SET_COOKIE);
        Assert.notNull(setCookies, "当前header没有Set-Cookie属性");
        String userToken = null;
        String phpsessid = null;
        int maxAge = Integer.MAX_VALUE;
        for (String cookies : setCookies) {
            if (cookies.startsWith(USER_KEY)) {
                userToken = cookies.split(";")[0].split("=")[1];
                maxAge = Math.min(maxAge, Integer.parseInt(cookies.split(";")[2].split("=")[1]));
            }
            if (cookies.startsWith(PHPSESSID_KEY)) {
                phpsessid = cookies.split(";")[0].split("=")[1];
                maxAge = Math.min(maxAge, Integer.parseInt(cookies.split(";")[2].split("=")[1]));
            }
        }
        if (!(StringUtils.hasText(userToken) && StringUtils.hasText(phpsessid)))
            throw new IllegalArgumentException("Set-Cookie首部缺少下面字段之一:[" + USER_KEY + "," + PHPSESSID_KEY + "]");
        return new MomoCookies(userToken, phpsessid, maxAge);
    }

}