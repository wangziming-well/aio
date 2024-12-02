package com.wzm.aio.media;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import com.wzm.aio.service.JavService;
import com.wzm.aio.util.JacksonUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RssFeedGenerator {



    public static void main(String[] args) {
        try {
            // 创建 RSS feed 对象
            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType("rss_2.0");
            feed.setTitle("jav个人订阅");
            feed.setLink("http://localhsot:8080/");
            feed.setDescription("javdb网站中watchedVideo的RSS订阅");
            feed.setPublishedDate(new Date());

            URL resource = JacksonUtils.class.getClassLoader().getResource("temp.json");
            String path = resource.getPath();
            ArrayList<JavService.VideoItem> videoItems = JacksonUtils.readValue(path, new TypeReference<>() {});


            // 创建 RSS feed 项目列表
            List<SyndEntry> entries = new ArrayList<>();

            for (JavService.VideoItem videoItem : videoItems) {
                SyndEntry entry = new SyndEntryImpl();
                entry.setTitle(videoItem.getAvCode() + " "+videoItem.getTitle());
                entry.setLink(videoItem.getTorrentItem().getMagnetUrl());
                SyndContent description = new SyndContentImpl();
                description.setType("text/plain");
                description.setValue(videoItem.getAvCode() + " "+videoItem.getTitle());
                entry.setDescription(description);
                entry.setPublishedDate(new Date());
                entries.add(entry);
            }

            // 设置项目列表到 RSS feed
            feed.setEntries(entries);

            // 输出 RSS feed 到文件
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, new FileWriter("rss_feed.xml"));

            System.out.println("RSS feed 已生成");
        } catch (IOException | FeedException e) {
            e.printStackTrace();
        }
    }
}
