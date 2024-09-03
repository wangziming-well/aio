package com.wzm.aio.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public abstract class JacksonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();


    public static Map<String,Object> toMap(Object obj){
        return mapper.convertValue(obj, new TypeReference<>() {});

    }

    public static Map<String,String> toStringMap(Object obj){
        return mapper.convertValue(obj, new TypeReference<>() {});
    }

    public static Map<String,String> toMap(String str)  {
        try {
            return mapper.readValue(str, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


}
