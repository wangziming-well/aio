package com.wzm.aio.util;

import lombok.Getter;

//PartOfSpeech词性
@Getter
public enum PartOfSpeech {
    NOUN("名词", "表示人、地方、事物、概念等"),
    PRONOUN("代词", "替代名词，用来指代人或事物"),
    VERB("动词", "表示动作或状态"),
    ADJECTIVE("形容词", "描述或修饰名词，表示名词的特征或性质"),
    ADVERB("副词", "修饰动词、形容词或其他副词，表示时间、方式、程度等"),
    DETERMINER("限定词", "位于名词前，限定名词的范围或数量"),
    PREPOSITION("介词", "表示名词或代词与句中其他词语的关系，通常表示位置、时间或方式"),
    CONJUNCTION("连词", "连接词语、短语或句子，表示它们之间的关系"),
    INTERJECTION("感叹词", "表达情感或情绪的短语或词语，通常独立于句子结构"),
    MODAL_VERB("情态动词", "表示可能性、能力、许可或必要性"),
    ARTICLE("冠词", "特定类型的限定词，表示名词的确定性或不确定性"),

    NUll("非单词","非英语单词");


    private final String chineseName;
    private final String description;

    PartOfSpeech(String chineseName, String description) {
        this.chineseName = chineseName;
        this.description = description;
    }
}