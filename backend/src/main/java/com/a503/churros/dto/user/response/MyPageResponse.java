package com.a503.churros.dto.user.response;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MyPageResponse {

    private String result;

    private Map<String,Object> userInfo;

    @Builder
    public MyPageResponse(String result, String name, String email, Integer provider, String imageUrl,Boolean activate) {
        this.result = result;
        this.userInfo = new HashMap<>();
        this.userInfo.put("name",name);
        this.userInfo.put("email",email);
        this.userInfo.put("provider",providerIntegerToString(provider));
        this.userInfo.put("imageUrl",imageUrl);
        this.userInfo.put("activate",activate);


    }

    private String providerIntegerToString(Integer provider){
       String[] converter = {"nothing","local","naver","kakao","google"};

       return converter[provider];
    }
}
