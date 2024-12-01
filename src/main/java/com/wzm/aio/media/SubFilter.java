package com.wzm.aio.media;

import com.wzm.aio.util.FileUtils;

import java.io.File;

public class SubFilter {
    public static void main(String[] args) {
        File folder = new File("E:\\Temp\\[诸神字幕组] 冰上的尤里");
        FileUtils.traverseFile(folder,SubFilter::filter);
    }


    public static void filter(File file){
        String s = "简日";
        String name = file.getName();
        if (!name.contains(s))
            file.delete();

    }
}
