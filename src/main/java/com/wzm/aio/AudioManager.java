package com.wzm.aio;

import com.wzm.aio.util.AudioConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ws.schild.jave.Encoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;

/**
 * 本地音频文件管理器
 */
public class AudioManager {

    private static final Log logger = LogFactory.getLog(AudioManager.class);

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        threadLocal.set(new Encoder());
        String rootPath = "D:\\AIO\\其他-英语 - 副本 (3)";
        recursion(new File(rootPath));
        executorService.shutdown();
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            long end = System.currentTimeMillis();
            logger.info("total: " + (end -start)/1000 + "s");
        }));
    }

    private static final ThreadLocal<Encoder> threadLocal = new ThreadLocal<>();

    private static Encoder getEncoder() {
        Encoder encoderExist = threadLocal.get();
        if (encoderExist != null)
            return encoderExist;
        Encoder encoder = new Encoder();
        threadLocal.set(encoder);
        return encoder;
    }

    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);


    public static void recursion(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            for (File childFile : files) {
                recursion(childFile);
            }
        } else {
            executorService.submit(() -> dealFile(file));
        }
    }

    private static void dealFile(File file) {
        try {
            long start = System.currentTimeMillis();
            String sourceName = file.getName();
            file = dealAudioFileName(file);
            file = dealAudioFormat(file);
            long end = System.currentTimeMillis();
            String targetName = file.getName();
            logger.info("处理音频文件:[" + sourceName + "]->[" + targetName + "];spend:" + (end - start) + " ms");
        } catch (Exception e) {
            logger.info("报错：" + e.getMessage(), e);
        }

    }

    //转换音频文件格式,统一转换为wav格式
    private static File dealAudioFormat(File file) {
        if (file.getName().endsWith(".wav"))
            return file;
        Encoder encoder = getEncoder();//获取当前线程上的encoder，保证线程安全，该encoder 会在线程启动时被绑定到线程上
        //将非wav格式的文件转换为wav
        File result;
        try {
            result = AudioConverter.convertToWAV(file, encoder);
            Files.delete(file.toPath());
        } catch (IOException  e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private static File dealAudioFileName(File file) {
        String sourceName = file.getName();
        String[] split = sourceName.split("\\.");
        String fileName = split[0]; //文件名
        String fileExtension = split[1]; //文件后缀
        if (!fileName.contains("-"))
            return file;
        String[] split1 = fileName.split("-");
        fileName = split1[split.length - 1];
        String targetName = fileName.trim() + "." + fileExtension;
        String parentDir = file.getParentFile().getPath();
        Path source = Path.of(parentDir, sourceName);
        Path target = Path.of(parentDir, targetName);

        try {
            Files.move(source, target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return target.toFile();
    }


}
