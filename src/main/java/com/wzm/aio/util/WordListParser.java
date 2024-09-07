package com.wzm.aio.util;

import org.apache.logging.log4j.util.Strings;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class WordListParser {
    private static final String WORD_SEP = " ";

    private static final char[] SEPARATOR = new char[]{';', '；', ',', '，', '.', '。'};

    private static boolean isSeparator(char c) {
        for (char value : SEPARATOR) {
            if (value == c)
                return true;
        }
        return false;
    }

    public static List<String> parse(String text) {
        if (!StringUtils.hasText(text))
            return Collections.emptyList();
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (isSeparator(chars[i]))
                chars[i] = WORD_SEP.charAt(0);
        }
        String s = new String(chars);
        return Arrays.stream(s.split(WORD_SEP))
                .filter(StringUtils::hasText)
                .map(String::trim)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
    }

    public static String join(List<String> strings){
        return String.join(WORD_SEP,strings);
    }

}
