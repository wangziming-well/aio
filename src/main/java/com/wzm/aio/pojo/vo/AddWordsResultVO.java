package com.wzm.aio.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddWordsResultVO {

    private List<String> parsedWords;

    private List<String> newWords;

    private List<String> existedWords;

}
