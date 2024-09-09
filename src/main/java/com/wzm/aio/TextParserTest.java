package com.wzm.aio;

import com.wzm.aio.util.TextParser;

import java.util.List;

public class TextParserTest {

    public static String text ="action";

    public static void main(String[] args){
        long start = System.currentTimeMillis();
        List<TextParser.ResultEntry> resultEntries = TextParser.parse(text);
        long end = System.currentTimeMillis();
        System.out.println("total:" + (end - start));

        for (TextParser.ResultEntry entry :resultEntries){
            System.out.printf("%-20s %-20s %-15s %-15s\n",entry.getWord(),entry.getLemma(),entry.getPos(),entry.getPosTag());
        }


    }
}