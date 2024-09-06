package com.wzm.aio.util;

import com.wzm.aio.domain.MomoCookies;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MomoCookiesHolder {



    private MomoCookiesHolder(){}

    public static final MomoCookiesHolder INSTANCE = new MomoCookiesHolder();

    private volatile MomoCookies momoCookies = null;

    public void set(MomoCookies momoCookies){
        this.momoCookies = momoCookies;
    }

    public void clear(){
        this.momoCookies = null;
    }
    //判断当前holder是否为空
    public boolean isEmpty() {
        return this.momoCookies == null;
    }
    //当前cookies是否可用
    public boolean isActive(){
        MomoCookies cookies = this.momoCookies;
        return cookies != null && !cookies.isExpire();
    }


    public MomoCookies get() {
        return this.momoCookies;
    }
}
