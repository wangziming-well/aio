package com.wzm.aio.config;

import com.wzm.aio.api.frdic.FrWord;
import com.wzm.aio.properties.ProjectProperties;
import com.wzm.aio.service.FrDicService;
import com.wzm.aio.service.MomoService;
import com.wzm.aio.util.SpringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

import java.util.List;

/**
 * spring生命周期配置
 */
@Configuration
public class SpringLifecycleConfiguration {

    private static final Log logger = LogFactory.getLog(SpringLifecycleConfiguration.class);

    private final ApplicationContext applicationContext;

    private final MomoService momoService;

    private final FrDicService frDicService;

    private final ProjectProperties projectProperties;

    public SpringLifecycleConfiguration(ApplicationContext applicationContext,
                                        MomoService momoService, FrDicService frDicService,
                                        ProjectProperties projectProperties) {
        this.applicationContext = applicationContext;
        this.momoService = momoService;
        this.frDicService = frDicService;
        this.projectProperties = projectProperties;
    }

    //springboot初始化完毕回调
    @Bean
    public CommandLineRunner commandLineRunner() {
        ProjectProperties.Init init = projectProperties.getInit();
        return args -> {
            logger.info("springboot加载完毕，进行项目初始化回调");
            initProcess();
            if (init.isMomoPull())
                pullMomoToLocal();
            if (init.isFrSync())
                syncWordsToMomo();
            logger.info("初始化回调完成");

        };
    }

    private void initProcess() {
        try {
            Class.forName("com.wzm.aio.util.TextParser");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        SpringUtils.setApplicationContext(applicationContext);
    }

    private void pullMomoToLocal() {
        momoService.pull();
    }

    private void syncWordsToMomo() {
        List<FrWord> allWords = frDicService.getAllWords("0");
        List<String> wordList = allWords.stream().map(FrWord::getWord).toList();
        momoService.addWordsToNotepad("Code", wordList);
    }

    @Bean
    public ApplicationListener<ContextClosedEvent> contextClosedEventApplicationListener() {
        ProjectProperties.Closed closed = projectProperties.getClosed();

        return event -> {
            logger.info("spring容器关闭回调开始");
            if (closed.isMomoPush())
                momoService.push();
            logger.info("spring容器关闭回调结束");
        };
    }

}
