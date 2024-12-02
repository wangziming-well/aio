package com.wzm.aio.util;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.function.Consumer;

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

    public static void move(File source, File target) {
        try {
            Files.move(source.toPath(), target.toPath());
        } catch (IOException e) {
            throw new RuntimeException("文件移动失败", e);
        }
    }

    /**
     * 遍历指定文件夹中的文件，并对文件通过callback处理
     * @param folder 要遍历的文件夹
     * @param callback 对文件的处理回调
     */

    public static void traverseFile(File folder, Consumer<File> callback){
        if (!folder.exists() || !folder.isDirectory())
            throw  new RuntimeException("指定的路径不是一个有效的文件夹！");
        traverseFileInternal(folder,callback);
    }

    private static void traverseFileInternal(File folder, Consumer<File> callback){
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                // 递归处理子文件夹
                traverseFileInternal(file,callback);
            } else {
                // 处理文件
                callback.accept(file);
            }
        }
    }

    public static String fileExtension(File file){
        if (!file.isFile())
            throw new RuntimeException("不是文件");
        // 获取文件名
        String fileName = file.getName();
        String fileExtension = "";

        // 查找最后一个点
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = fileName.substring(dotIndex + 1);
        }

        return fileExtension;
    }

    public static String baseName(File file){
        if (!file.isFile())
            throw new RuntimeException("不是文件");
        // 获取文件名
        String fileName = file.getName();
        String fileBaseName = "";
        // 查找最后一个点
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileBaseName = fileName.substring(0,dotIndex);
        }
        return fileBaseName;
    }

    /**
     * 例如： 123.default.app.ass 的中间名是 .default.app
     * @param file 文件
     * @return 返回文件的中间名
     */

    public static String middleName(File file){
        if (!file.isFile())
            throw new RuntimeException("不是文件");
        // 获取文件名
        String fileName = file.getName();
        String middleName = "";
        int lastIndex = fileName.lastIndexOf('.');
        int firstIndex = fileName.indexOf('.');
        if (firstIndex > 0){
            middleName = fileName.substring(firstIndex,lastIndex);
        }
        return middleName;
    }

    public static void main(String[] args) {
        File file = new File("O:\\Anime\\动漫\\电波女与青春男 (2011)\\Season 1\\[VCB-Studio] Denpa Onna to Seishun Otoko [02][Ma10p_1080p][x265_flac_aac].Commentary.ass");
        System.out.println(middleName(file));
    }


    public static void renameFile(File file,String newFilename) {
        // 获取文件所在目录
        String parentPath = file.getParent();
        File renamedFile = new File(parentPath, newFilename);
        // 重命名文件
        if (file.renameTo(renamedFile)) {
            System.out.println("文件已重命名：" + file.getAbsolutePath() + " -> " + renamedFile.getAbsolutePath());
        } else {
            System.err.println("文件重命名失败：" + file.getAbsolutePath());
        }
    }

}
