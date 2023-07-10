package com.a503.churros.entity.auth.mapping;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
public class TokenMapping  {
    private String userEmail;




    private String accessToken;
    private String refreshToken;

    private Date createdDate;
    private Date accessTokenExpire;
    private Date refreshTokenExpire;

    public TokenMapping(){}

    @Builder
    public TokenMapping(String userEmail, String accessToken, String refreshToken,Date createdDate,Date accessTokenExpire, Date refreshTokenExpire){
        this.userEmail = userEmail;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.createdDate = createdDate;
        this.accessTokenExpire = accessTokenExpire;
        this.refreshTokenExpire = refreshTokenExpire;
    }

}
