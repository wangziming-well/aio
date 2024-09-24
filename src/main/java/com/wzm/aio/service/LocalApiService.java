package com.wzm.aio.service;

import com.wzm.aio.api.local.LocalOpenApi;
import org.springframework.stereotype.Service;

@Service
public class LocalApiService {

    private final LocalOpenApi api;

    public LocalApiService(LocalOpenApi api) {
        this.api = api;
    }

    public void momoPull(){
        api.momoPull();
    }

    public void momoPush(){
        api.momoPush();
    }

    public void syncFrWordsToMomo(){
        api.syncWord();
    }

}
