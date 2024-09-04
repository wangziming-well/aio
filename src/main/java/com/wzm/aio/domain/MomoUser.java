package com.wzm.aio.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MomoUser {
    private String email;
    private String password;
}
