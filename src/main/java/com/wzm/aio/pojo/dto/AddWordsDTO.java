package com.wzm.aio.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddWordsDTO {
    @NotNull(message = "notNull")
    private String text;

    private String localId;

    private String title;

}
