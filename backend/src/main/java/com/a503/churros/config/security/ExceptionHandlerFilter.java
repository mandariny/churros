package com.a503.churros.config.security;

import com.a503.churros.config.security.advice.payload.ErrorCode;
import com.a503.churros.service.user.CustomTokenProviderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Autowired
    private CustomTokenProviderService customTokenProviderService;

    @Value("${app.auth.tokenSecret}")
    private  String secretKey;
    private String accessToken ;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("RefreshTokenValidation");

        if(StringUtils.hasText(bearerToken)){
            accessToken = bearerToken.substring(7, bearerToken.length());
            try {

                Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken);


            } catch ( MalformedJwtException ex) {
                log.error("1. 잘못된 JWT 방식입니다.");
                setErrorResponse(response, ErrorCode.MALFORMED_JWT);
                return;
            } catch (io.jsonwebtoken.security.SecurityException ex) {
                log.error("2. 잘못된 JWT 서명입니다.");
                setErrorResponse(response, ErrorCode.BAD_SIGNATURE);
                return;
            } catch (ExpiredJwtException ex) {
                log.error("3. 만료된 JWT 토큰입니다.");
                setErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
                return;
            } catch (UnsupportedJwtException ex) {
                log.error("4. 지원되지 않는 JWT 토큰입니다.");
                setErrorResponse(response, ErrorCode.NOT_SUPPORTED_TOKEN);
                return;
            } catch (IllegalArgumentException ex) {
                log.error("5. JWT 토큰이 잘못되었습니다.");
                setErrorResponse(response, ErrorCode.INVALID_TOKEN);
                return;
            }catch(Exception ex){
                log.error("6. 나머지 JWT 오류 ");

                setErrorResponse(response, ErrorCode.OTHER_JWT_ERROR);
                return;
            }

            try{
                UsernamePasswordAuthenticationToken authentication = customTokenProviderService.getAuthenticationById(accessToken);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch(Exception ex){
                log.error("인증 오류입니다.");
                setErrorResponse(response, ErrorCode.AUTHENTICATION_ERROR);
                return;
            }


        }

        if(StringUtils.hasText(refreshToken)){
            try {

                Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(refreshToken);


            } catch ( MalformedJwtException ex) {
                log.error("1. 잘못된 refresh JWT 방식입니다.");
                setErrorResponse(response, ErrorCode.MALFORMED_JWT_REFRESH);
                return;
            } catch (io.jsonwebtoken.security.SecurityException ex) {
                log.error("2. 잘못된 refresh JWT 서명입니다.");
                setErrorResponse(response, ErrorCode.BAD_SIGNATURE_REFRESH);
                return;
            } catch (ExpiredJwtException ex) {
                log.error("3. 만료된 refresh JWT 토큰입니다.");
                setErrorResponse(response, ErrorCode.TOKEN_EXPIRED_REFRESH);
                return;
            } catch (UnsupportedJwtException ex) {
                log.error("4. 지원되지 않는 refresh JWT 토큰입니다.");
                setErrorResponse(response, ErrorCode.NOT_SUPPORTED_TOKEN_REFRESH);
                return;
            } catch (IllegalArgumentException ex) {
                log.error("5. refresh JWT 토큰이 잘못되었습니다.");
                setErrorResponse(response, ErrorCode.INVALID_TOKEN_REFRESH);
                return;
            }catch(Exception ex){
                log.error("6. 나머지 refresh JWT 오류 ");

                setErrorResponse(response, ErrorCode.OTHER_JWT_ERROR_REFRESH);
                return;
            }
        }
        filterChain.doFilter(request, response);

    }


    private void setErrorResponse(
            HttpServletResponse response,
            ErrorCode errorCode
    ){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(403);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getStatus(), errorCode.getMessage());
        try{


            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
        catch (IOException e){

            e.printStackTrace();
        }
        catch (Exception e){

            e.printStackTrace();
        }

    }

    @Data
    public static class ErrorResponse{
        private final Integer code;
        private final String message;
    }
}
