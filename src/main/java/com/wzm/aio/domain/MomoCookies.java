package com.wzm.aio.domain;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MomoCookies {

    private MomoCookies(){}

    public static final String USER_KEY = "userToken";

    public static final String PHPSESSID_KEY = "PHPSESSID";

    public static final MomoCookies INSTANCE = new MomoCookies();

    private volatile MultiValueMap<String,String> cookiesMap;
    private volatile boolean empty = true;
    private final ReadWriteLock  lock = new ReentrantReadWriteLock();

    public void fill(List<String> cookieStrings){
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            for(String cookies : cookieStrings){
                if (cookies.startsWith(MomoCookies.USER_KEY)){
                    map.put(USER_KEY,List.of(cookies.split(";")[0].split("=")[1]) );
                }
                if (cookies.startsWith(MomoCookies.PHPSESSID_KEY)){
                    map.put(PHPSESSID_KEY,List.of(cookies.split(";")[0].split("=")[1]) );

                }
            }
            if (map.size() == 2){
                this.empty = false;
                this.cookiesMap = map;
            }
            else
                clear();

        } catch (Exception e){
            clear();
            throw new RuntimeException("填充MomoCookies失败",e);
        } finally {
            writeLock.unlock();
        }
    }

    public void clear(){
        Lock writeLock = lock.writeLock();
        try{
            writeLock.lock();
            this.cookiesMap = null;
            this.empty = true;
        }  finally {
            writeLock.unlock();
        }
    }

    public boolean isEmpty() {
        Lock readLock = lock.readLock();
        boolean result;
        try {
            readLock.lock();
            result = this.empty;
        } finally {
            readLock.unlock();
        }
        return result;
    }

    public MultiValueMap<String, String> getCookiesMap() {
        Lock readLock = lock.readLock();
        MultiValueMap<String, String> result;
        try {
            readLock.lock();
            result = this.cookiesMap;
        } finally {
            readLock.unlock();
        }
        return result;
    }
}
