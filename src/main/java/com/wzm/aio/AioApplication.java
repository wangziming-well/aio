package com.wzm.aio;

import com.wzm.aio.api.entity.MomoCloudNotepad;
import com.wzm.aio.mapper.MomoNotepadMapper;
import com.wzm.aio.service.MomoCloudService;
import com.wzm.aio.service.MomoLocalService;
import com.wzm.aio.service.MomoService;
import lombok.SneakyThrows;
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
    private static String id = "np-UcgS1JEXkbpSPLNKOeHjRGs4rioocUyKagCOsYiiNeDwP7TgUc3RNLuaM6FCHF64";

    public static void main(String[] args) {
       ConfigurableApplicationContext run = SpringApplication.run(AioApplication.class, args);
        MomoLocalService service = run.getBean(MomoLocalService.class);
    }

    @SneakyThrows
    public static void deleteAll(MomoCloudService service){
        List<MomoCloudNotepad> allMomoCloudNotepads = service.getAllNotepads();
        for(MomoCloudNotepad momoCloudNotepad : allMomoCloudNotepads){
            Thread.sleep(1000);
            System.out.println(momoCloudNotepad.getId());
            System.out.println(service.deleteNotepad(momoCloudNotepad.getId()));
        }
    }

}
