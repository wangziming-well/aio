package com.wzm.aio.api.momo;

import lombok.Data;

import java.util.List;

@Data
public class MomoResponse<T> {

    private boolean success;
    private List<Error> errors;
    private T data;

    @Data
    public static class Error{
        private String code;
        private String msg;
        private String info;
    }

}
