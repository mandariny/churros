package com.a503.churros.service.user;

import com.a503.churros.entity.auth.mapping.TokenMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


public interface CustomTokenProviderService {

    public TokenMapping createToken(Authentication authentication);
    public boolean validateToken(String token);
    public Long getUserIdFromToken(String token);

    public UsernamePasswordAuthenticationToken getAuthenticationById(String token);

    public UsernamePasswordAuthenticationToken getAuthenticationByEmail(String email);

}
