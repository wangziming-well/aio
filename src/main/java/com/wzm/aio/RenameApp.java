package com.wzm.aio;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameApp {

    public static void main(String[] args) {
        // 指定文件夹路径
        String folderPath = "P:\\Anime\\火影忍者：疾风传";

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("指定的路径不是一个有效的文件夹！");
            return;
        }

        // 遍历文件夹并重命名文件
        traverseAndRenameFiles(folder);


    }


    private static void traverseAndRenameFiles(File folder) {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // 递归处理子文件夹
                traverseAndRenameFiles(file);
            } else {
                // 对文件进行重命名
                renameFile(file);
            }
        }
    }


    private static int [] session1 = new int[] {52,52,54,62};

    private static int [] session2 = new int[] {32,21,18,17,24,31,8,24,21,25,21,33,20,25,28,13,11,21,20,87};

    private static String fromCountToEpisode(int count){
        int sum = 0;
        int i = 0;
        for (; i < session2.length; i++) {
            sum += session2[i];
            if (count <= sum)
                break;
        }
        int episode = count - (sum - session2[i]);
        int session = i+1;
        return String.format("S%02dE%02d",session,episode);
    }


    private static String session1episode(File file){
        String name = file.getName();
        Pattern compile = Pattern.compile("\\[(\\d*?)]");
        Matcher matcher = compile.matcher(name);

        if (matcher.find()){
            String episode = matcher.group(1);
            int i = Integer.parseInt(episode);
            return  fromCountToEpisode(i);
        }
        Pattern compile1 = Pattern.compile("\\[(\\d*?)-(\\d*?)]");
        Matcher matcher1 = compile1.matcher(name);
        if (matcher1.find()){
            String episode1 = matcher1.group(1);
            String episode2 = matcher1.group(2);
            int i = Integer.parseInt(episode1);
            int j = Integer.parseInt(episode2);
            return fromCountToEpisode(i)+fromCountToEpisode(j);
        }
        return "";
    }

    private static void renameFile(File file) {
        // 获取文件所在目录
        String parentPath = file.getParent();
        String episode = session1episode(file);

        // 创建新文件名
        String newFileName = String.format("火影忍者：疾风传[%s].mkv",episode);
        File renamedFile = new File(parentPath, newFileName);

        // 重命名文件
        if (file.renameTo(renamedFile)) {
            System.out.println("文件已重命名：" + file.getAbsolutePath() + " -> " + renamedFile.getAbsolutePath());
        } else {
            System.out.println("文件重命名失败：" + file.getAbsolutePath());
        }
    }

}
