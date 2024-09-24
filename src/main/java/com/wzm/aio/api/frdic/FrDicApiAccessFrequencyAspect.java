package com.wzm.aio.api.frdic;

import com.wzm.aio.util.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 限制DrDicOpenApi的访问频率:
 * 1分钟内访问需要少于29次
 * 30分钟内访问需要少于499次
 * 采用惰性删除策略，当有主动访问的api是，才会检查access列表中是否有过期的访问
 * 存储的访问队列实现持久化存储，以能在程序间记录
 */
@Aspect
@Component
public class FrDicApiAccessFrequencyAspect implements DisposableBean {

    private static final Log logger = LogFactory.getLog(FrDicApiAccessFrequencyAspect.class);

    private static final int frequencyOn60s = 29;

    private static final long periodOn60s = 60 * 1000L; // 单位ms

    private static final int frequencyOn30m = 499;

    private static final long periodOn30m = 30 * 60 * 1000L; // 单位ms

    private final String basePath ;

    private final LinkedList<Long> accessIn60s;

    private static final String serializeFileNameIn60s = "accessIn60s";
    private final LinkedList<Long> accessIn30m;

    private static final String serializeFileNameIn30m = "accessIn30m";

    @SuppressWarnings("unchecked")
    public FrDicApiAccessFrequencyAspect(@Value("${serialized.file.path}") String basePath) {
        this.basePath =  basePath;
        accessIn60s =(LinkedList<Long>) FileUtils.deserialize(new File(basePath, serializeFileNameIn60s),LinkedList.class);
        logger.info("反序列化accessIn60s:" + accessIn60s);
        accessIn30m =(LinkedList<Long>) FileUtils.deserialize(new File(basePath, serializeFileNameIn30m),LinkedList.class);
        logger.info("反序列化accessIn30m:" + accessIn30m);

    }


    @Before("execution(* com.wzm.aio.api.frdic.FrDicOpenApi.*(..))")
    public void advice() {
        long current = System.currentTimeMillis();
        checkFrequency(current);
    }

    private synchronized void checkFrequency(long current) {
        int accessCountIn60s = checkFrequencyLimit(accessIn60s, periodOn60s, frequencyOn60s, current);
        int accessCountIn3m = checkFrequencyLimit(accessIn30m, periodOn30m, frequencyOn30m, current);
        logger.debug( "frDicApi访问统计:"+
                (periodOn60s / 1000) + "s内访问["+ accessCountIn60s+"]次;"+
                (periodOn30m / 1000) + "s内访问["+ accessCountIn3m+"]次" );
        accessIn60s.add(current);
        accessIn30m.add(current);
    }

    private int checkFrequencyLimit(Queue<Long> accessQueue, long period, int frequencyLimit, long currentTime) {
        while (!accessQueue.isEmpty() && currentTime - accessQueue.peek() >= period) {
            accessQueue.remove();
        }
        if (accessQueue.size() >= frequencyLimit) {
            throw new RuntimeException("访问频率过高,高于" + (period / 1000) + "s" + frequencyLimit + "次");
        }
        return accessQueue.size();
    }

    @Override
    public synchronized void destroy() {
        logger.info("开始序列化");
        File parentDir = new File(basePath);
        if (!parentDir.exists()) {
            boolean mkdirs = parentDir.mkdirs();
            if (!mkdirs)
                throw new RuntimeException("创建失败");
        }
        logger.info("序列化accessIn60s:" + accessIn60s);
        FileUtils.serialize(new File(basePath, serializeFileNameIn60s),accessIn60s);
        logger.info("序列化accessIn30m:" + accessIn30m);
        FileUtils.serialize(new File(basePath, serializeFileNameIn30m),accessIn30m);
        logger.info("序列化完成");


    }
}
