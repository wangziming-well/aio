package com.wzm.aio.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MomoCookies {
    public static final String USER_KEY = "userToken";

    public static final String PHPSESSID_KEY = "PHPSESSID";

    @JsonProperty(USER_KEY)
    private String userToken;
    @JsonProperty(PHPSESSID_KEY)
    private String phpsessid;

    private boolean empty;

    public static final MomoCookies INSTANCE = new MomoCookies();

}
