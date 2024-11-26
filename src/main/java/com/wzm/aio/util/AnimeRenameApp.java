package com.wzm.aio.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimeRenameApp {


    public static void main(String[] args) {
       // 指定文件夹路径
        String folderPath = "P:\\Xunlei\\SW笔记";
        String animeName = "死亡笔记";
        int [] seasonEpisodeCount = null;
        int episodeType =2;
        int offset = 0;
        boolean isTest = true;
        fileRename(folderPath,animeName,episodeType,seasonEpisodeCount,offset,isTest);
    }

    public static void fileRename(String folderPath, String animeName, int episodeType, int [] seasonEpisodeCount, int offset, boolean test){
        File folder = new File(folderPath);
        FileUtils.traverseFile(folder, file -> fileRename(file,animeName,episodeType,seasonEpisodeCount, offset,test) );
    }




    public static void fileRename(File file, String animeName, int episodeType, int [] seasonEpisodeCount, int offset, boolean test){
        String oldName = file.getName();
        String extension = oldName.split("\\.")[1];
        String seasonAndEpisodeCode = getSeasonAndEpisodeCode(oldName,episodeType,seasonEpisodeCount, offset);
        String newFilename = String.format("%s[%s].%s",animeName,seasonAndEpisodeCode,extension);
        if (test)
            System.out.println("预览："+oldName + "->" + newFilename);
        else
            renameFile(file,newFilename);
    }

    public static String fromSxxExx(String filename){
        Pattern compile = Pattern.compile("\\[S\\d*E\\d*S\\d*E\\d*]");
        Matcher matcher = compile.matcher(filename);

        if (matcher.find()){
            return matcher.group();
        }

        compile = Pattern.compile("\\[S\\d*E\\d*]");
        matcher = compile.matcher(filename);

        if (matcher.find()){
            return matcher.group();
        }
        return "";
    }

    public static String fromXxxToOneSeason(String filename,int offset){
        return fromXxxToMultiSeason(filename,new int[] {Integer.MAX_VALUE}, offset);
    }

    public static String getSeasonAndEpisodeCode(String filename,int episodeType,int [] seasonEpisodeCount,int offset){
        return switch (episodeType) {
            case 1 -> fromSxxExx(filename);
            case 2 -> fromXxxToOneSeason(filename, offset);
            case 3 -> fromXxxToMultiSeason(filename, seasonEpisodeCount, offset);
            default -> throw new RuntimeException("不支持的episodeType");
        };
    }

    private static String fromXxxToMultiSeason(String filename, int[] seasonEpisodeCount,int offset) {
        Pattern compile = Pattern.compile("\\[(\\d*)-(\\d*)]");
        Matcher matcher = compile.matcher(filename);

        if (matcher.find()){
            String number1 = matcher.group(1);
            String number2 = matcher.group(2);
            return   calculateSeasonEpisode(Integer.parseInt(number1) + offset,seasonEpisodeCount)
                    + calculateSeasonEpisode(Integer.parseInt(number2) + offset,seasonEpisodeCount);
        }

        compile = Pattern.compile("\\[(\\d*)]");
        matcher = compile.matcher(filename);

        if (matcher.find()){
            String number = matcher.group(1);
            return calculateSeasonEpisode(Integer.parseInt(number)+ offset,seasonEpisodeCount);
        }
        return "";
    }


    private static String calculateSeasonEpisode(int count, int[] seasonEpisodeCount){
        int sum = 0;
        int i = 0;
        for (; i < seasonEpisodeCount.length; i++) {
            sum += seasonEpisodeCount[i];
            if (count <= sum)
                break;
        }
        int episode = count - (sum - seasonEpisodeCount[i]);
        int session = i+1;
        return String.format("S%02dE%02d",session,episode);
    }


    private static void renameFile(File file,String newFilename) {
        // 获取文件所在目录
        String parentPath = file.getParent();
        File renamedFile = new File(parentPath, newFilename);
        // 重命名文件
        if (file.renameTo(renamedFile)) {
            System.out.println("文件已重命名：" + file.getAbsolutePath() + " -> " + renamedFile.getAbsolutePath());
        } else {
            System.out.println("文件重命名失败：" + file.getAbsolutePath());
        }
    }

}
