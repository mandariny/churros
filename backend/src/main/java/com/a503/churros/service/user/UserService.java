package com.a503.churros.service.user;


import com.a503.churros.dto.auth.request.SignInRequest;
import com.a503.churros.dto.auth.request.SignUpRequest;
import com.a503.churros.dto.auth.response.AuthResponse;
import com.a503.churros.dto.auth.response.MessageResponse;
import com.a503.churros.dto.user.response.MyPageResponse;
import com.a503.churros.entity.user.User;
import net.minidev.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public interface UserService {

    public AuthResponse signin(SignInRequest signInRequest);
    public MessageResponse signup(SignUpRequest signUpRequest);

    public Optional<User> kakaoSignup(JSONObject resp);

    public void kakaoAuthorize(HttpServletResponse response) throws IOException;

    public void kakaoCallBack(String code, HttpServletResponse response) throws IOException;

    public MyPageResponse myPage(Long userIdx);

    public MessageResponse activate(Long userIdx);

    public HashMap<String,String> refresh(String refreshToken);

    public MessageResponse logout(Long userIdx);

    public MessageResponse deleteUser(Long userIdx);

}
