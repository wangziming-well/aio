package com.wzm.aio.api.frdic;

import lombok.Data;

import java.util.List;

@Data
public class FrWordsBook {
    private String id;
    private String language;
    private String name;
    private List<String> words;

}
