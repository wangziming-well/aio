package com.wzm.aio.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service
public class CloudDictService {

    private final Log logger = LogFactory.getLog(CloudDictService.class);

    private final DictionaryService  dictionaryService;
    private final MomoService momoService;

    public CloudDictService(DictionaryService dictionaryService, MomoService momoService){
        this.dictionaryService = dictionaryService;
        this.momoService = momoService;
    }

    public int addWord(String[] words){
        int count =  0;
        for(String word : words){
            if (dictionaryService.addWord(word))
                count++;

        }
        return count;
    }


}
