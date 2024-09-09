package com.wzm.aio.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class AddWordsResultVO {

    private List<String> parsedWords;

    private List<String> newWords;

    private List<String> existedWords;

}
