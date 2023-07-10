package com.a503.churros.feign.auth;

import net.minidev.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name="getKakaoInfo" ,url = "https://kapi.kakao.com")
public interface GetKakaoInfoFeign {
    @PostMapping(value="/v2/user/me",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
            )

    JSONObject  getKakaoInfo(@RequestHeader("Authorization") String accessToken);
}
