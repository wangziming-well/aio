package com.wzm.aio.api.local;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface LocalOpenApi {

    @GetExchange("/momo/pull")
    void momoPull();

    @GetExchange("/momo/push")
    void momoPush();

    @GetExchange("/frdic/syncWord")
    void syncWord();

}
