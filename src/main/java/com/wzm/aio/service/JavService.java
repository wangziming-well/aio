package com.wzm.aio.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wzm.aio.api.javdb.JavDBRequester;
import com.wzm.aio.util.ThreadUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class JavService {
    private final JavDBRequester requester;

    private static final Log logger = LogFactory.getLog(JavService.class);

    private static final long sleepTime = 1000L;



    public JavService(JavDBRequester requester) {
        this.requester = requester;
    }

    private Document parse(ResponseEntity<String> response) {
        if (response.getStatusCode().isError())
            throw new RuntimeException("响应错误:" + response);
        String body = response.getBody();
        assert body != null;
        return Jsoup.parse(body);
    }

    public void homePage() {
        ResponseEntity<String> homepage = requester.homepage();
        Document body = parse(homepage);
        System.out.println(body.title());
        System.out.println(body.body());
    }

    public List<String> watchedVideosId() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 1; ; i++) {
            ResponseEntity<String> response = requester.watchedVideos(i);
            Document doc = parse(response);
            Element empty = doc.selectFirst("#videos > .empty-message");
            if (empty != null)
                break;

            Elements select = doc.select("#videos > div");
            for (Element element : select) {
                result.add(element.id().split("-")[1]);
            }
            logger.info("成功采集第" + i + "页");
            ThreadUtils.sleep(sleepTime);
        }
        return result;
    }

    public ArrayList<VideoItem> allWatchedVideo(){
        List<String> ids = watchedVideosId();
        ArrayList<VideoItem> result = new ArrayList<>();
        for (String id : ids){
            VideoItem video;
            try {
                 video = video(id);
            } catch (Exception e){


                throw new RuntimeException("检索["+id+"]时发生错误",e);
            }
            result.add(video);
            logger.info("检索成功:id["+id +"] 番号["+video.getAvCode() +"] 标题["+ video.getTitle() +"]" );
            ThreadUtils.sleep(sleepTime);
        }
        return result;
    }

    public VideoItem video(String javId) {
        ResponseEntity<String> response = requester.video(javId);
        Document document = parse(response);
        Elements elements = document.select("#magnets-content > div");
        Elements titles = document.select(".title > strong");
        String avCode = titles.get(0).text();
        String title = titles.get(1).text();
        ArrayList<TorrentItem> torrentItems = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            TorrentItem item = parse(elements.get(i), i);
            torrentItems.add(item);
        }
        TorrentItem torrentItem = selectItem(torrentItems);
        return VideoItem.builder().javId(javId).avCode(avCode)
                .title(title).torrentItem(torrentItem).build();

    }

    private TorrentItem selectItem(ArrayList<TorrentItem> torrentItems) {
        //优先无码破解
        for(TorrentItem item : torrentItems){
            if (item.getName().endsWith(".无码破解"))
                return item;
        }
        //其次高清
        for(TorrentItem item : torrentItems){
            if (item.getTag().contains("高清") || item.getTag().contains("HD"))
                return item;
        }
        return torrentItems.get(0);
    }
    private String text(Element element){
        if ( element == null)
            return "";
        return element.text();
    }

    private TorrentItem parse(Element element, int index) {
        String magnetUrl = element.selectFirst(".magnet-name > a").attr("href");
        String name = text(element.selectFirst(".magnet-name > a > .name"));
        String meta =text(element.selectFirst(".magnet-name > a > .meta"));
        Elements tagElements = element.select(".magnet-name > a > .tags > span");
        List<String> tags = parseTags(tagElements);
        String time = text(element.selectFirst(".date > span"));

        return TorrentItem.builder().index(index).magnetUrl(magnetUrl)
                .name(name).meta(meta).updateTime(LocalDate.parse(time)).tag(tags).build();

    }

    private List<String> parseTags(Elements tagElements) {
        ArrayList<String> result = new ArrayList<>();
        for (Element element : tagElements) {
            result.add(element.text());
        }
        return result;
    }

    //5.78GB, 4個文件 -> 5.78GB
    private String parseSize(String meta) {
        return meta.split(",")[0];
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TorrentItem {
        private int index;
        private String name;
        private String magnetUrl;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate updateTime;
        private String meta;
        private List<String> tag;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VideoItem{
        private String title; //标题
        private String avCode; //番号
        private String javId;
        private TorrentItem torrentItem;


    }


}
