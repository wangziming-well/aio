package com.wzm.aio.util;

import org.springframework.util.Assert;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtils {



    /**
     * 将file复制到指定的文件夹下
     * 如果目标文件夹已存在文件， 则覆盖该文件
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


    public static boolean mkdir(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            return directory.mkdirs();

        }
        return false;
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
        if (!source.isDirectory()) {
            throw new RuntimeException("source不是文件夹");
        }
        if (!target.exists()) {
            boolean result = target.mkdirs();
            if (!result)
                throw new RuntimeException("创建文件夹失败");
        }
        String[] sourceFiles = source.list();
        for (String sourceFilename : sourceFiles) {
            File sourceFile = new File(source, sourceFilename);
            File targetFile = new File(target, sourceFilename);
            if (sourceFile.isDirectory()) {
                copyDir(sourceFile, targetFile);
            } else {
                copy(sourceFile,targetFile);
            }
        }
    }

    public static void copyDir(Path source,Path target){
        copyDir(source.toFile(),target.toFile());
    }

    public static void main(String[] args) throws IOException {
        File file = new File("D:\\Data\\Learning\\note");
        File target = new File("D:\\AIO\\note");
        FileSystemUtils.copyRecursively(file,target);
        //copyDir(file, target);
    }

}
