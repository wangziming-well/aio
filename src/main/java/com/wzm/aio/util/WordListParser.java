package com.wzm.aio.util;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class WordListParser {
    private static final String WORD_SEP = " ";



    public static List<String> parse(String text) {
        if (!StringUtils.hasText(text))
            return Collections.emptyList();
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!isLetter(chars[i])) //将所有的非字母符号替换成空格
                chars[i] = WORD_SEP.charAt(0);
        }
        String s = new String(chars);
        ArrayList<String> result = new ArrayList<>();
        Arrays.stream(s.split(WORD_SEP))
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .forEach(str ->dealWords(str,result));
        return result.stream().distinct().sorted().collect(Collectors.toList());
    }
    //处理分割好的单词,如果word是驼峰命名格式的多个单词拼接，那么对其进行拆分，分别加入collect
    private static void dealWords(String word ,List<String> collect){
        if (isCamelCase(word)){
            dealCamelCase(word,collect);
        } else {
            addWord(word,collect);
        }
    }

    private static void addWord(String word,List<String> collect){
        if (word.length() <= 1)
            return;
        collect.add(word.toLowerCase());
    }

    private static void dealCamelCase(String word, List<String> collect) {
        char[] chars = word.toCharArray();

        int startIndex = 0;
        int endIndex = 0;

        for (int i = 0; i < chars.length; i++) {
            if (isUpperCase(chars[i]) || i == chars.length -1){
                endIndex = i;
                if (startIndex != endIndex){
                    String s = new String(chars, startIndex, endIndex - startIndex).toLowerCase();
                    addWord(s,collect);
                    startIndex = endIndex;
                }
            }
        }

    }

    //只要单词的非首字母有大写的，就算是驼峰单词，例如 DAOs,hasMax等
    private static boolean isCamelCase(String word){
        char[] chars = word.toCharArray();
        if (chars.length <=1)
            return false;
        int upperCaseCount = isUpperCase(chars[0]) ? 1 : 0;
        boolean hasUpperCaseInMiddle = false;
        for (int i = 1; i < chars.length; i++) {
            if (isUpperCase(chars[i])){
                upperCaseCount ++;
                hasUpperCaseInMiddle = true;
            }
        }
        if (upperCaseCount == chars.length) //全大写单词不是驼峰
            return false;
        if (upperCaseCount == chars.length -1) //只有一个字母是小写的也不算驼峰
            return false;

        return hasUpperCaseInMiddle;
    }

    private static boolean isLetter(char c){
        return (65 <= c && c <= 90) || (97 <= c && c <= 122);
    }

    private static boolean isNumber(char c){
        return 48 <= c && c <= 57;

    }

    private static boolean isLowerCase(char c){
        return 97 <= c && c <= 122;
    }

    private static boolean isUpperCase(char c){
        return 65 <= c && c <= 90;
    }



    public static String join(List<String> strings){
        strings.sort(String::compareTo);
        return String.join(WORD_SEP,strings);
    }

}
