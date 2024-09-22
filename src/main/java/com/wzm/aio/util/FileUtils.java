package com.wzm.aio.util;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class FileUtils {


    /**
     * 将file复制到指定的文件夹下
     * 如果目标文件夹已存在文件， 则覆盖该文件
     *
     * @param source 要复制的文件
     * @param target 要复制到的文件
     */

    public static void copy(File source, File target) {
        try {
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("文件复制失败", e);
        }
    }


    public static void copy(File source, String targetDir) {
        String name = source.getName();
        File target = new File(targetDir, name);
        copy(source, target);
    }


    public static void mkdirs(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            boolean result = directory.mkdirs();
            if (!result)
                throw new RuntimeException("创建文件夹[" + path + "]失败");
        }
    }

    /**
     * 将source代表的文件夹的内容复制到target代表的文件夹中
     * 如果target不存在，则创建对应目录
     * 如果文件要复制的文件已存在，则覆盖
     *
     * @param source 源文件夹
     * @param target 目标文件夹
     */

    public static void copyDir(File source, File target) {
        copyDir(source, target, null);
    }

    public static void copyDir(Path source, Path target) {
        copyDir(source.toFile(), target.toFile(), null);
    }

    public static void copyDir(File source, File target, String ignoreDirName) {
        if (!source.isDirectory()) {
            throw new RuntimeException("source不是文件夹");
        }
        if (!target.exists()) {
            boolean result = target.mkdirs();
            if (!result)
                throw new RuntimeException("创建文件夹失败");
        }
        String[] sourceFiles = source.list();
        assert sourceFiles != null;
        for (String sourceFilename : sourceFiles) {
            File sourceFile = new File(source, sourceFilename);
            File targetFile = new File(target, sourceFilename);
            if (sourceFile.isDirectory()) {
                if (StringUtils.hasText(ignoreDirName) && Objects.equals(ignoreDirName, sourceFile.getName()))
                    continue; // 如果文件夹名是要忽略的，跳过本次循环
                copyDir(sourceFile, targetFile);
            } else {
                copy(sourceFile, targetFile);
            }
        }
    }

    public static void copyDir(Path source, Path target, String ignoreDirName) {
        copyDir(source.toFile(), target.toFile(), ignoreDirName);
    }

    /**
     * 删除目录，包括其中的子文件夹和文件
     *
     * @param directory 指定的目录
     */
    public static void deleteDir(Path directory) {
        Assert.notNull(directory, "directory不能为空");
        File file = directory.toFile();
        if (!file.exists())
            return;
        if (file.isFile())
            throw new RuntimeException("directory不是文件夹");
        File[] files = file.listFiles();
        assert files != null;
        for (File childFile : files) {
            if (childFile.isFile()) {
                try {
                    Files.delete(childFile.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                deleteDir(childFile.toPath());
            }

        }


    }

    public static void serialize(File file, Object object) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(File file, Class<T> clazz) {
        if (!file.exists() || !file.isFile()) {
            try {
                return clazz.getConstructor().newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            Object o = objectInputStream.readObject();
            if (clazz.isInstance(o)) {
                return clazz.cast(o);
            } else {
                throw new RuntimeException("期望类型[" + clazz.getName() + "]和实际类型[" + o.getClass().getName() + "]不匹配");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
