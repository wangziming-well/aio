package com.wzm.aio;

import com.wzm.aio.api.MomoOpenApi;
import com.wzm.aio.api.MomoResponse;
import com.wzm.aio.api.entity.Notepad;
import com.wzm.aio.api.entity.NotepadList;
import com.wzm.aio.api.entity.OneNotepad;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan({ "com.wzm.aio.properties" })
public class AioApplication {

    public static void main(String[] args) {
       ConfigurableApplicationContext run = SpringApplication.run(AioApplication.class, args);
        MomoOpenApi api = run.getBean(MomoOpenApi.class);
        OneNotepad build = OneNotepad.toUpdate().title("new").brief("brief")
                .content("apple hello brief safe cave").build();
        String id = "np-Xk4AubCFMprHbrmwtk__ZSAjqx6I7sAy_v1WIvKvymSPq7EChP9L6F5VucxjvViq";
        ResponseEntity<MomoResponse<NotepadList>> result = api.getAllNotepads();
    }

}
