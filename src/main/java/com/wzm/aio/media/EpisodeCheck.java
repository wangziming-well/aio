package com.wzm.aio.media;

import com.wzm.aio.util.FileUtils;
import org.springframework.data.relational.core.sql.In;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EpisodeCheck {

    public static void main(String[] args) {
        File folder = new File("P:\\standalone\\海贼王系列");
        int[] ints = new int[1123];
        FileUtils.traverseFile(folder, file -> {
            int index = getIndex(file);
             ints[index] ++;
        });
        System.out.println(Arrays.toString(ints));
        for (int i = 1; i <= 1122; i++) {
            if (ints[i] != 1)
                System.out.println(i + " " + ints[i]);
        }

    }

    public static int getIndex(File file){
        String name = file.getName();

        Pattern compile = Pattern.compile("\\[(\\d*)]");
        Matcher matcher = compile.matcher(name);
        if (matcher.find()){
            String numberStr = matcher.group(1);
            return Integer.parseInt(numberStr);
        }

        compile = Pattern.compile("-(\\d*)");
        matcher = compile.matcher(name);
        if (matcher.find()){
            String numberStr = matcher.group(1);
            return Integer.parseInt(numberStr);
        }

        throw new RuntimeException("解析index失败:" + name);
    }
}
