package com.wzm.aio;

import com.wzm.aio.service.DocusaurusService;
import com.wzm.aio.util.SpringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.*;

@SpringBootApplication
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan({ "com.wzm.aio.properties" })
public class AioApplication {


    public static void main(String[] args) throws ClassNotFoundException, IOException {
        ConfigurableApplicationContext run = SpringApplication.run(AioApplication.class, args);
        Class.forName("com.wzm.aio.util.TextParser");
        SpringUtils.setApplicationContext(run);
        File file = new File("D:\\AIO\\command.log");
        FileOutputStream outputStream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        DocusaurusService service = run.getBean(DocusaurusService.class);
        service.setCommandOutputConsumer(s -> {
            try {
                writer.write(s + "\n");
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        service.loadDocusaurusWeb();
        writer.close();
    }

}
