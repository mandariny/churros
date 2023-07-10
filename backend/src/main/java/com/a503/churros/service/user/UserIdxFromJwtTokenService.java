package com.a503.churros.service.user;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserIdxFromJwtTokenService {

    @Value("${app.auth.tokenSecret}")
    private String secretKey;


    public Long extractIdxFromToken(String accessToken){
        accessToken = accessToken.substring(7, accessToken.length());

        Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken);
        Long userIdx = Long.parseLong( jws.getBody().get("sub",String.class));

        return userIdx;
    }



}
