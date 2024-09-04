package com.wzm.aio;

import com.wzm.aio.domain.MomoNotepad;
import com.wzm.aio.domain.MomoNotepadInfo;
import com.wzm.aio.domain.User;
import com.wzm.aio.service.MomoService;
import org.springframework.aop.framework.Advised;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.List;

@SpringBootApplication
@EnableAspectJAutoProxy
public class AioApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(AioApplication.class, args);
        MomoService momoService = run.getBean(MomoService.class);
        momoService.login(new User("441678514@qq.com", "wang998321"));
        List<MomoNotepadInfo> allNotepad = momoService.getAllNotepad();
        System.out.println(allNotepad);
        momoService.logout();
    }

}
