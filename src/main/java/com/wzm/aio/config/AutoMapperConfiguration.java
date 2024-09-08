package com.wzm.aio.config;

import com.wzm.aio.util.WordListParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AutoMapperConfiguration {

    public static class StringListConverter{

        public List<String> convertToList(String str){
            return WordListParser.parse(str);
        }

        public String convertToString(List<String> lists){
            return WordListParser.join(lists);
        }

    }
    @Bean
    public StringListConverter stringListConverter(){
        return new StringListConverter();
    }


}
