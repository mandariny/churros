package com.a503.churros.service.user;

import com.a503.churros.config.security.JwtConfig;
import com.a503.churros.config.security.UserPrincipal;
import com.a503.churros.entity.auth.mapping.TokenMapping;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;


@Slf4j
@Service
public class CustomTokenProviderServiceImpl implements CustomTokenProviderService{

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    public TokenMapping createToken(Authentication authentication) {
        // userPrincipal은 spring security의 email 부분
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
//        log.info("hahahahaahahahahahahah");
       //  log.info(userPrincipal.toString());
        Date now = new Date();
        // 좀 찍어보자
        Date accessTokenExpiresIn = new Date(now.getTime() + jwtConfig.getAuth().getAccessTokenExpirationMsec());
        Date refreshTokenExpiresIn = new Date(now.getTime() + jwtConfig.getAuth().getRefreshTokenExpirationMsec());

        String secretKey = jwtConfig.getAuth().getTokenSecret();
//        log.info(secretKey);
//        log.info(oAuth2Config.getAuth().toString());
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        log.info(Arrays.toString(keyBytes));
        Key key = Keys.hmacShaKeyFor(keyBytes);

        log.info("bbb");
        // Long.toString(userPrincipal.getId())

        // sub 엔 유저 id, iat엔 시작시점, exp 엔 만료되는 시점
        String accessToken = Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
        // refreshtoken엔 exp 만료되는 시점만 , 추후 변경 가능
        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenMapping.builder()
                .userEmail(userPrincipal.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .createdDate(now)
                .accessTokenExpire(accessTokenExpiresIn)
                .refreshTokenExpire(refreshTokenExpiresIn)
                .build();
    }

    public boolean validateToken(String token) {
        try {
            //log.info("bearerToken = {} \n oAuth2Config.getAuth()={}", token, oAuth2Config.getAuth().getTokenSecret());
            Jwts.parserBuilder().setSigningKey(jwtConfig.getAuth().getTokenSecret()).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException ex) {
            log.error("1. 잘못된 JWT 서명입니다.");
            log.info(ex.toString());
            throw new io.jsonwebtoken.security.SecurityException("잘못");
        } catch (MalformedJwtException ex) {
            log.error("2. 잘못된 JWT 서명입니다.");
            log.info(ex.toString());
            throw new IOException("2222");
        } catch (ExpiredJwtException ex) {
            log.error("3. 만료된 JWT 토큰입니다.");
            log.info(ex.toString());
            // 여기에 access-token 갱신
            throw new IOException("3333");
        } catch (UnsupportedJwtException ex) {
            log.error("4. 지원되지 않는 JWT 토큰입니다.");
            throw new IOException("4444");
        } catch (IllegalArgumentException ex) {
            log.error("5. JWT 토큰이 잘못되었습니다.");
            throw new IOException("5555");
        }

    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtConfig.getAuth().getTokenSecret())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }
    public UsernamePasswordAuthenticationToken getAuthenticationById(String token){

        Long userId = getUserIdFromToken(token);

        UserDetails userDetails = customUserDetailsService.loadUserById(userId);

        UsernamePasswordAuthenticationToken authenticationPasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        return authenticationPasswordAuthenticationToken;
    }

    public UsernamePasswordAuthenticationToken getAuthenticationByEmail(String email){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authenticationPasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        return authenticationPasswordAuthenticationToken;
    }
}
