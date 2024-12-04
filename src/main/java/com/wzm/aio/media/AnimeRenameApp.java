package com.wzm.aio.media;

import com.wzm.aio.util.FileUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimeRenameApp {


    public static void main(String[] args) {
        app();
    }

    public static void app(){
        // 指定文件夹路径
        String folderPath = "O:\\Anime\\动漫\\绝望先生\\[celery] Zoku Sayonara Zetsubou Sensei (S02)";
        String animeName = "绝望先生";
        int [] seasonEpisodeCount = null;
        int episodeType =2;
        int offset = 0;
        int season = 2;
        boolean isTest = false;
        task(folderPath,animeName,episodeType,season,seasonEpisodeCount,offset,isTest);
    }

    public static void task(String folderPath, String animeName, int episodeType,int season, int [] seasonEpisodeCount, int offset, boolean test){
        File folder = new File(folderPath);
        FileUtils.traverseFile(folder, file -> fileRename(file,animeName,episodeType, season,seasonEpisodeCount, offset,test) );
    }




    public static void fileRename(File file, String animeName, int episodeType,int season, int [] seasonEpisodeCount, int offset, boolean test){
        String oldName = file.getName();
        String extension = FileUtils.fileExtension(file);
        String seasonAndEpisodeCode = getSeasonAndEpisodeCode(oldName,episodeType, season,seasonEpisodeCount, offset);
        String newFilename = String.format("%s %s.%s",animeName,seasonAndEpisodeCode,extension);
        if (test)
            System.out.println("预览："+oldName + "->" + newFilename);
        else
            renameFile(file,newFilename);
    }

    public static String fromSxxExx(String filename,int season,int offset){
        Pattern compile = Pattern.compile("S(\\d*)E(\\d*)S(\\d*)E(\\d*)");
        Matcher matcher = compile.matcher(filename);

        if (matcher.find()){
            int E1 =Integer.parseInt(matcher.group(2)) ;
            int E2 =Integer.parseInt(matcher.group(4)) ;

            return String.format("S%02dE%02dS%02dE%02d",season,E1 + offset,season,E2 + offset);
        }

        compile = Pattern.compile("S\\d*E(\\d*)");
        matcher = compile.matcher(filename);

        if (matcher.find()){
            int E1 =Integer.parseInt(matcher.group(1)) ;
            return String.format("S%02dE%02d",season,E1 + offset);

        }

        compile = Pattern.compile("\\.(\\d*)\\.");
        matcher = compile.matcher(filename);

        if (matcher.find()){
            int E1 =Integer.parseInt(matcher.group(1)) ;
            return String.format("S%02dE%02d",season,E1 + offset);
        }

        compile = Pattern.compile("\\[(\\d*)]");
        matcher = compile.matcher(filename);

        if (matcher.find()){
            int E1 =Integer.parseInt(matcher.group(1)) ;
            return String.format("S%02dE%02d",season,E1 + offset);
        }

        compile = Pattern.compile("-(\\d*)");
        matcher = compile.matcher(filename);

        if (matcher.find()){
            int E1 =Integer.parseInt(matcher.group(1)) ;
            return String.format("S%02dE%02d",season,E1 + offset);
        }

        return "";
    }

    public static String fromXxxToOneSeason(String filename,int season,int offset){
        int[] ints = new int[season];
        ints[season-1] = Integer.MAX_VALUE;
        return fromXxxToMultiSeason(filename,ints, offset);
    }

    public static String getSeasonAndEpisodeCode(String filename,int episodeType,int season,int [] seasonEpisodeCount,int offset){
        return switch (episodeType) {
            case 1 -> fromSxxExx(filename, season, offset);
            case 2 -> fromXxxToOneSeason(filename, season, offset);
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
        compile = Pattern.compile("第(\\d*)話");
        matcher = compile.matcher(filename);

        if (matcher.find()){
            String number = matcher.group(1);
            return calculateSeasonEpisode(Integer.parseInt(number)+ offset,seasonEpisodeCount);
        }

        compile = Pattern.compile("第(\\d*)集");
        matcher = compile.matcher(filename);

        if (matcher.find()){
            String number = matcher.group(1);
            return calculateSeasonEpisode(Integer.parseInt(number)+ offset,seasonEpisodeCount);
        }

        compile = Pattern.compile("E(\\d*)");
        matcher = compile.matcher(filename);

        if (matcher.find()){
            String number = matcher.group(1);
            return calculateSeasonEpisode(Integer.parseInt(number)+ offset,seasonEpisodeCount);
        }

        compile = Pattern.compile("-(\\d*)");
        matcher = compile.matcher(filename);

        if (matcher.find()){
            String number = matcher.group(1);
            return calculateSeasonEpisode(Integer.parseInt(number)+ offset,seasonEpisodeCount);
        }


        throw new RuntimeException("匹配集数失败:" + filename);
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
