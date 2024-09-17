package com.wzm.aio;

import com.wzm.aio.service.DocusaurusService;
import com.wzm.aio.util.SpringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.*;
import java.util.function.Consumer;

@SpringBootApplication
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan({ "com.wzm.aio.properties" })
public class AioApplication {


    public static void main(String[] args) throws ClassNotFoundException {
        ConfigurableApplicationContext run = SpringApplication.run(AioApplication.class, args);
        Class.forName("com.wzm.aio.util.TextParser");
        SpringUtils.setApplicationContext(run);
        DocusaurusService service = run.getBean(DocusaurusService.class);
        service.setCommandOutputConsumer(System.out::println);
        service.loadDocusaurusWeb();
    }

    public static Consumer<String> outputToFile(String path){
        File file = new File(path);
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        return s -> {
            try {
                writer.write(s + "\n");
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

}
