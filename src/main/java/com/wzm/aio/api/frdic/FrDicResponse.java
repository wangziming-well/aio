package com.wzm.aio.api.frdic;

import lombok.Data;

@Data
public class FrDicResponse<T> {

    private String message;

    private T data;

}
