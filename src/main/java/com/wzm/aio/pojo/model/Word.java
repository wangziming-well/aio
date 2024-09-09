package com.wzm.aio.pojo.model;


import lombok.Data;

@Data
public class Word {

    private  int id;
    private String word;

    public Word(){
    }
    public Word(String word){
        this.word = word;
    }

}
