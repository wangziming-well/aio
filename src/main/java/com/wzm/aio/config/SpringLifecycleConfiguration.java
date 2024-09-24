package com.wzm.aio.config;

import com.wzm.aio.properties.ProjectProperties;
import com.wzm.aio.service.LocalApiService;
import com.wzm.aio.util.SpringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

/**
 * spring生命周期配置
 */
@Configuration
public class SpringLifecycleConfiguration {

    private static final Log logger = LogFactory.getLog(SpringLifecycleConfiguration.class);

    private final ApplicationContext applicationContext;

    private final LocalApiService service;

    private final ProjectProperties projectProperties;

    public SpringLifecycleConfiguration(ApplicationContext applicationContext,
                                        LocalApiService service, ProjectProperties projectProperties) {
        this.applicationContext = applicationContext;
        this.service = service;
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
                service.momoPull();
            if (init.isFrSync())
                service.syncFrWordsToMomo();
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

    @Bean
    public ApplicationListener<ContextClosedEvent> contextClosedEventApplicationListener() {
        ProjectProperties.Closed closed = projectProperties.getClosed();

        return event -> {
            logger.info("spring容器关闭回调开始");
            if (closed.isMomoPush())
                service.momoPush();
            logger.info("spring容器关闭回调结束");
        };
    }

}
