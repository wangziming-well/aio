package com.wzm.aio.domain;

import lombok.Data;

@Data
public class MomoNotepadRequestBody {

    private String keyword =null;
    private String scope = "MINE";
    private boolean recommend =  false;
    private int offset = 0;
    private int limit = 100;
    private int total = -1;
}
