package com.a503.churros.feign.auth;


import net.minidev.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;



@FeignClient(name="getKakaoToken" ,url = "https://kauth.kakao.com")
public interface GetKakaoTokenFeign {

    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)

    JSONObject getKakaoToken(@RequestParam("grant_type") String grantType, @RequestParam("client_id") String clientId,
                             @RequestParam("redirect_uri") String redirectUri, @RequestParam("code") String code);


    @PostMapping("/v2/user/me"
    )

    JSONObject  getKakaoInfo(@RequestHeader("Authorization") String accessToken);
}
