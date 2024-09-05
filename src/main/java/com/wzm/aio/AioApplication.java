package com.wzm.aio;

import com.wzm.aio.domain.MomoNotepad;
import com.wzm.aio.domain.MomoNotepadInfo;
import com.wzm.aio.domain.MomoUser;
import com.wzm.aio.domain.Word;
import com.wzm.aio.mapper.DictionaryMapper;
import com.wzm.aio.properties.MomoProperties;
import com.wzm.aio.service.DictionaryService;
import com.wzm.aio.service.MomoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.List;

@SpringBootApplication
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan({ "com.wzm.aio.properties" })
public class AioApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(AioApplication.class, args);
        MomoService service = run.getBean(MomoService.class);
        service.login(new MomoUser("441678514@qq.com","wang998321"));
        service.getAllNotepad();
        service.logout();
        /*MomoNotepad build = MomoNotepad.builder().id("3533394").title("编程用").brief("test").content("hello world").build();
        System.out.println(service.saveNotepad(build));
        service.logout();
        System.out.println(service.saveNotepad(build));*/
    }

}
