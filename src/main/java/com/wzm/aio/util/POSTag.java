package com.wzm.aio.util;

import lombok.Getter;
import lombok.ToString;

@Getter
public enum POSTag {
    //名词
    NN("单数普通名词", "dog, car, idea", PartOfSpeech.NOUN),
    NNS("复数普通名词", "dogs, cars, ideas", PartOfSpeech.NOUN),
    NNP("单数专有名词", "John, London, Google", PartOfSpeech.NOUN),
    NNPS("复数专有名词", "Americans, Googles", PartOfSpeech.NOUN),
    FW("外来词", "d'accord, faux pas", PartOfSpeech.NOUN),
    CD("基数词", "one, two, 100", PartOfSpeech.NOUN),
    LS("列表标记", "1), 2), a.", PartOfSpeech.NOUN),
    //动词
    VB("基本形式动词", "run, eat, go", PartOfSpeech.VERB),
    VBD("过去式动词", "ran, ate, went", PartOfSpeech.VERB),
    VBG("现在分词/动名词", "running, eating", PartOfSpeech.VERB),
    VBN("过去分词", "eaten, gone", PartOfSpeech.VERB),
    VBP("非第三人称单数现在时动词", "run, eat", PartOfSpeech.VERB),
    VBZ("第三人称单数现在时动词", "runs, eats", PartOfSpeech.VERB),
    //情态动词
    MD("情态动词", "can, should, will", PartOfSpeech.MODAL_VERB),
    //形容词
    JJ("形容词", "big, yellow, amazing", PartOfSpeech.ADJECTIVE),
    JJR("比较级形容词", "bigger, faster", PartOfSpeech.ADJECTIVE),
    JJS("最高级形容词", "biggest, fastest", PartOfSpeech.ADJECTIVE),
    //限定词
    DT("限定词", "the, a, an", PartOfSpeech.DETERMINER),
    PDT("前限定词", "all, both, half", PartOfSpeech.DETERMINER),
    WDT("限定性疑问词", "which, that", PartOfSpeech.DETERMINER),
    CC("并列连词", "and, but, or", PartOfSpeech.CONJUNCTION),
    //f副词
    RB("副词", "quickly, silently", PartOfSpeech.ADVERB),
    RBR("比较级副词", "faster, better", PartOfSpeech.ADVERB),
    RBS("最高级副词", "fastest, best", PartOfSpeech.ADVERB),
    EX("存在性引导词", "there (e.g., there is)", PartOfSpeech.ADVERB),
    WRB("疑问副词", "where, when", PartOfSpeech.ADVERB),
    //介词
    IN("介词/从属连词", "in, of, like, because", PartOfSpeech.PREPOSITION),
    TO("不定式引导词", "to (e.g., to go)", PartOfSpeech.PREPOSITION),
    RP("小品词", "up, off, over (e.g., give up)", PartOfSpeech.PREPOSITION),
    //代词
    POS("所有格标记", "'s", PartOfSpeech.PRONOUN),
    PRP("人称代词", "I, you, he, she", PartOfSpeech.PRONOUN),
    PRP_S("所有格代词", "my, your, her, their", PartOfSpeech.PRONOUN),
    WP("疑问代词", "who, what", PartOfSpeech.PRONOUN),
    WP_S("所有格疑问代词", "whose", PartOfSpeech.PRONOUN),
    //感叹词
    UH("感叹词", "uh, wow, oh, hey", PartOfSpeech.INTERJECTION),
    //无实意
    SYM("符号", "$, %, +, =", PartOfSpeech.NUll),
    NULL("非标签", ".,;", PartOfSpeech.NUll);

    private final String description;
    private final String example;
    private final PartOfSpeech partOfSpeech;

    POSTag(String description, String example, PartOfSpeech partOfSpeech) {
        this.description = description;
        this.example = example;
        this.partOfSpeech = partOfSpeech;
    }

    public PartOfSpeech pos() {
        return partOfSpeech;
    }

    public static POSTag fromString(String str){
        try {
            return POSTag.valueOf(str.replace("$","_S"));
        } catch (IllegalArgumentException e){
            return NULL;
        }
    }

}