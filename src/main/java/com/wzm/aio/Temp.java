package com.wzm.aio;

import com.wzm.aio.util.FileUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Temp {
    private static final String targetPath = "E:\\Temp\\迅雷";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String s = scanner.nextLine();
            if (StringUtils.hasText(s)){
                String s1 = s.split("//")[1].split(":")[0];
                System.out.println(s1);
            }
        }
        //recursion(new File("E:\\Video\\av-for-emby"));
    }

    public static void recursion(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            for (File f : files) {
                recursion(f);
            }
        } else {
            deal2(file);
        }



    }

    private static void deal2(File file){
        String name = file.getName();
        if (name.endsWith(".nfo")){
            try {
                Path path = file.toPath();
                System.out.println("删除:" +path);
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void deal(File file) {
        String name = file.getName();
        if (name.endsWith("mp4")){
            String parent = file.getParentFile().getName();
            Path target = Path.of(targetPath, parent+".mp4");
            FileUtils.move(file,target.toFile());
            System.out.println("move:" + file + ">" + target);
        }
    }
}
