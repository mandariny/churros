package com.a503.churros.repository.auth;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Repository
public interface CustomAuthorizationRequestRepository extends AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request);


    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response);

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request);
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response);
}
