package com.a503.churros.controller.user;


import com.a503.churros.dto.auth.request.SignInRequest;
import com.a503.churros.dto.auth.request.SignUpRequest;
import com.a503.churros.dto.auth.response.MessageResponse;
import com.a503.churros.service.user.UserIdxFromJwtTokenService;
import com.a503.churros.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserIdxFromJwtTokenService userIdxFromJwtTokenService;
    // Postman에서 사용
    @PostMapping("/signIn")
    public ResponseEntity<?> signin(@Valid @RequestBody SignInRequest signInRequest){
        return ResponseEntity.ok().body(userService.signin(signInRequest));
    }

    // Postman에서 사용
    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest){
        return ResponseEntity.ok().body(userService.signup(signUpRequest));
    }

    @GetMapping("/kakao")
    public void KakaoAuthorize(HttpServletResponse response) throws IOException {
        userService.kakaoAuthorize(response);
    }

    @RequestMapping(value = "/kakao/callback", produces = "application/json", method = {RequestMethod.GET,
            RequestMethod.POST})
   public void KakaoCallBack(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        userService.kakaoCallBack(code,response);
   }

    @GetMapping("")
    public ResponseEntity<?> myPage(@RequestHeader("Authorization") String token) throws Exception {
        // 서비스에서 가져오도록
        return ResponseEntity.ok().body(userService.myPage(userIdxFromJwtTokenService.extractIdxFromToken(token)));
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activate(@RequestHeader("Authorization") String token){
        Long userIdx = userIdxFromJwtTokenService.extractIdxFromToken(token);
        MessageResponse messageResponse = userService.activate(userIdx);
        return ResponseEntity.ok().body(messageResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("RefreshTokenValidation") String refreshToken){

        HashMap<String,String> result = userService.refresh(refreshToken);

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token){
        Long userIdx = userIdxFromJwtTokenService.extractIdxFromToken(token);
        MessageResponse messageResponse = userService.logout(userIdx);
        return ResponseEntity.ok().body(messageResponse);
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token){
        Long userIdx = userIdxFromJwtTokenService.extractIdxFromToken(token);
        MessageResponse messageResponse = userService.deleteUser(userIdx);
        return ResponseEntity.ok().body(messageResponse);
    }




}
