package com.wzm.aio.api.javdb;

import org.jsoup.nodes.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;


@HttpExchange
public interface JavDBRequester {

    @GetExchange
    ResponseEntity<String> homepage();

    @GetExchange("/users/watched_videos")
    ResponseEntity<String> watchedVideos(@RequestParam int page);

    @GetExchange("/v/{id}")
    ResponseEntity<String> video(@PathVariable String id);


}
