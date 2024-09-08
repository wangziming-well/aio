package com.wzm.aio;

import com.wzm.aio.api.entity.MomoCloudNotepad;
import com.wzm.aio.domain.MomoLocalNotepad;
import com.wzm.aio.domain.NotepadDictPair;
import com.wzm.aio.dto.MomoNotepadDTO;
import com.wzm.aio.mapper.MomoNotepadMapper;
import com.wzm.aio.mapper.NotepadDictMapper;
import com.wzm.aio.service.MomoCloudService;
import com.wzm.aio.service.MomoLocalService;
import com.wzm.aio.service.MomoService;
import com.wzm.aio.util.SpringUtils;
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
    private static String id = "np-mEKmVvdqyihHhW_v5u9_CzfDa__YnezpAniNjtnfaZqKN0BNxBtqJpj3DkNCugRt";

    public static void main(String[] args) {
       ConfigurableApplicationContext run = SpringApplication.run(AioApplication.class, args);
        SpringUtils.setApplicationContext(run);
        MomoService service = run.getBean(MomoService.class);
        MomoLocalService localService = run.getBean(MomoLocalService.class);
        MomoCloudService cloudService = run.getBean(MomoCloudService.class);
        MomoNotepadMapper notepadMapper = run.getBean(MomoNotepadMapper.class);
        NotepadDictMapper notepadDictMapper = run.getBean(NotepadDictMapper.class);
        MomoNotepadDTO momoNotepadDTO = new MomoNotepadDTO();
        momoNotepadDTO.setId(1);
        momoNotepadDTO.setTitle("ewts");
        service.push();
        System.exit(0);
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
