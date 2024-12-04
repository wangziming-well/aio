package com.wzm.aio.media;

import com.wzm.aio.util.FileUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubRename {

    public static void main(String[] args) {
        String folderStr = "O:\\Anime\\动漫\\绝望先生\\Season 3";
        File folder = new File(folderStr);
        boolean test =false;
        boolean useMidName = false;
        renameSub(folder,test,useMidName);
    }

    public static void renameSub(File folder,boolean test,boolean useMidName){
        Map<Integer, String> animeNames = animeNames(folder);
        FileUtils.traverseFile(folder , file -> {
            if (isSub(file)){
                String name = file.getName();
                int index = getIndex(name);
                String animeName = animeNames.get(index);
                String middleName =useMidName ? FileUtils.middleName(file) : "";
                middleName = StringUtils.hasText(middleName) ? middleName : ".default";
                String extension = FileUtils.fileExtension(file);
                String newName = String.format("%s%s.%s",animeName,middleName,extension);
                if (test)
                    System.out.println("预览: " + name + " ->" + newName  );
                else
                    FileUtils.renameFile(file,newName);
            }
        });
    }

    public static int getIndex(String name){
        Pattern compile = Pattern.compile("S\\d*E(\\d*)");
        Matcher matcher = compile.matcher(name);
        if (matcher.find()){
            String numberStr = matcher.group(1);
            return Integer.parseInt(numberStr);
        }
        compile = Pattern.compile("\\[(\\d*)]");
        matcher = compile.matcher(name);
        if (matcher.find()){
            String numberStr = matcher.group(1);
            return Integer.parseInt(numberStr);
        }

        compile = Pattern.compile("EP(\\d*)");
        matcher = compile.matcher(name);
        if (matcher.find()){
            String numberStr = matcher.group(1);
            return Integer.parseInt(numberStr);
        }
        compile = Pattern.compile("第(\\d*)話");
        matcher = compile.matcher(name);
        if (matcher.find()){
            String numberStr = matcher.group(1);
            return Integer.parseInt(numberStr);
        }

        compile = Pattern.compile("\\s(\\d*)\\s");
        matcher = compile.matcher(name);
        if (matcher.find()){
            String numberStr = matcher.group(1);
            return Integer.parseInt(numberStr);
        }

        compile = Pattern.compile("第(\\d*)话");
        matcher = compile.matcher(name);
        if (matcher.find()){
            String numberStr = matcher.group(1);
            return Integer.parseInt(numberStr);
        }

        compile = Pattern.compile("\\((\\d*)\\)");
        matcher = compile.matcher(name);
        if (matcher.find()){
            String numberStr = matcher.group(1);
            return Integer.parseInt(numberStr);
        }
        compile = Pattern.compile("- (\\d*)");
        matcher = compile.matcher(name);
        if (matcher.find()){
            String numberStr = matcher.group(1);
            return Integer.parseInt(numberStr);
        }
        compile = Pattern.compile("\\d*");
        matcher = compile.matcher(name);
        if (matcher.find()){
            String numberStr = matcher.group(0);
            return Integer.parseInt(numberStr);
        }


        throw new RuntimeException("解析index失败:" + name);
    }



    public static Map<Integer, String> animeNames(File folder){
        HashMap<Integer, String> result = new HashMap<>();

        FileUtils.traverseFile(folder,file -> {
            if (isVideo(file))
                result.put(getIndex(file.getName()),FileUtils.baseName(file));
        });

        return result;
    }

    public static boolean isSub(File file){
        String fileExtension = FileUtils.fileExtension(file);
        if (fileExtension.equals("ass"))
            return true;
        return false;
    }

    public static boolean isVideo(File file){
        String fileExtension = FileUtils.fileExtension(file);
        if (fileExtension.equals("mkv"))
            return true;
        if (fileExtension.equals("mp4"))
            return true;
        return false;
    }


}
