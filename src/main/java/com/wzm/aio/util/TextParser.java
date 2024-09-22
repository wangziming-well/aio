package com.wzm.aio.util;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.Getter;
import org.springframework.data.util.ParsingUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class TextParser {
    @Getter
    public static class ResultEntry {
        private final String word;
        private final POSTag posTag;
        private final PartOfSpeech pos;

        private final String lemma;//词根

        public ResultEntry(String word, POSTag posTag, PartOfSpeech pos, String lemma) {
            this.word = word;
            this.posTag = posTag;
            this.pos = pos;
            this.lemma = lemma;
        }
    }

    //根据词性过滤,过滤冠词、代词、和非单词
    //过滤单个字母
    public static class ResultFilter implements Predicate<ResultEntry> {

        @Override
        public boolean test(ResultEntry s) {
            if (s.getWord().length() == 1)
                return false;
            PartOfSpeech pos = s.getPos();
            return pos != PartOfSpeech.ARTICLE && pos != PartOfSpeech.PRONOUN && pos != PartOfSpeech.NUll;
        }
    }


    private static final String WORD_SEP = " ";

    private static final StanfordCoreNLP pipeline;

    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,pos,lemma");
        pipeline = new StanfordCoreNLP(props);
    }

    public static List<ResultEntry> parse(String text) {
        List<String> words = cleanAndTokenize(text);
        return annotateWords(words);
    }

    //使用npl标记word
    private static List<ResultEntry> annotateWords(List<String> words) {
        ArrayList<ResultEntry> result = new ArrayList<>(words.size());
        for (String word : words) {
            CoreDocument document = new CoreDocument(word);
            pipeline.annotate(document);
            List<CoreLabel> tokens = document.tokens();
            for (CoreLabel token : tokens) {
                POSTag posTag = POSTag.fromString(token.tag());
                result.add(new ResultEntry(token.word(), posTag, posTag.pos(), token.lemma()));
            }
        }
        return result;

    }

    //清理文本，并标记化分割处理，同时会分割驼峰式命名
    private static List<String> cleanAndTokenize(String text) {
        if (!StringUtils.hasText(text))
            return Collections.emptyList();
        text = filterNonLetter(text);
        return Arrays.stream(text.split(WORD_SEP))
                .filter(StringUtils::hasText) //过滤空字符串
                .map(String::trim)
                .map(TextParser::splitCamelCase)//拆分驼峰命名
                .flatMap(List::stream)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
    }

    //将text所有的非字母符号替换成空格
    private static String filterNonLetter(String text) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!isLetter(chars[i]))
                chars[i] = WORD_SEP.charAt(0);
        }
        return new String(chars);
    }

    //拆分驼峰命名单词
    private static List<String> splitCamelCase(String text) {
        if (isCamelCase(text))
            return ParsingUtils.splitCamelCase(text);
        return List.of(text);
    }

    //只要单词的非首字母有大写的，就算是驼峰单词，例如 DAOs,hasMax等
    private static boolean isCamelCase(String word) {
        char[] chars = word.toCharArray();
        if (chars.length <= 1)
            return false;
        int upperCaseCount = isUpperCase(chars[0]) ? 1 : 0;
        boolean hasUpperCaseInMiddle = false;
        for (int i = 1; i < chars.length; i++) {
            if (isUpperCase(chars[i])) {
                upperCaseCount++;
                hasUpperCaseInMiddle = true;
            }
        }
        if (upperCaseCount == chars.length) //全大写单词不是驼峰
            return false;
        if (upperCaseCount == chars.length - 1) //只有一个字母是小写的也不算驼峰
            return false;

        return hasUpperCaseInMiddle;
    }

    private static boolean isLetter(char c) {
        return (65 <= c && c <= 90) || (97 <= c && c <= 122);
    }

    private static boolean isUpperCase(char c) {
        return 65 <= c && c <= 90;
    }


}
