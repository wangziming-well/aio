package com.wzm.aio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AutoMapperConfiguration {

    public static class StringListConverter{

        private static final String DELIMITER = " ";

        public List<String> convertToList(String str){
            return List.of(str.split(DELIMITER));
        }

        public String convertToString(List<String> lists){
            return String.join(DELIMITER,lists);
        }

    }
    @Bean
    public StringListConverter stringListConverter(){
        return new StringListConverter();
    }


}
