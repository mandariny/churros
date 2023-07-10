package com.a503.churros.service.user;
import com.a503.churros.dto.auth.response.MessageResponse;
import com.a503.churros.dto.user.response.MyPageResponse;
import com.a503.churros.entity.auth.Token;
import com.a503.churros.entity.user.User;
import com.a503.churros.entity.auth.mapping.TokenMapping;
import com.a503.churros.dto.auth.request.SignInRequest;
import com.a503.churros.dto.auth.request.SignUpRequest;
import com.a503.churros.dto.auth.response.AuthResponse;
import com.a503.churros.feign.auth.GetKakaoInfoFeign;
import com.a503.churros.feign.auth.GetKakaoTokenFeign;
import com.a503.churros.repository.auth.TokenRepository;
import com.a503.churros.repository.user.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final GetKakaoTokenFeign getKakaoTokenFeign;

    private final GetKakaoInfoFeign getKakaoInfoFeign;

    private final CustomTokenProviderService customTokenProviderService;


    @Value("${kakao.client-id}")
    private String CLIENT_ID;
    @Value(("${kakao.client-secret}"))
    private String CLIENT_SECRET;


    @Value("${app.auth.tokenSecret}")
    private String secretKey;
    public AuthResponse signin(SignInRequest signInRequest){
        // 인증과정

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInRequest.getEmail(),
                            signInRequest.getPassword()
                    )
            );
            // 없음먄 BadCredentialsException
            // SecurityContextHolder는 Spring Security에서 사용되는 보안 관련 정보를 저장하는 객체 (주석처리 해도 수행은 되는데?)
            SecurityContextHolder.getContext().setAuthentication(authentication);

//        log.info(SecurityContextHolder.getContext().getAuthentication().toString());


            // 토큰 만드는 과정 customTokenProviderService에서 refreshToken 과 accessToken을 만들어 낸다.
            TokenMapping tokenMapping = customTokenProviderService.createToken(authentication);

            // 얘는 DB refreshToken-table 에 저장될 것
            Token token = Token.builder()
                    .refreshToken(tokenMapping.getRefreshToken())
                    .createdDate(tokenMapping.getCreatedDate())
                    .expireDate(tokenMapping.getRefreshTokenExpire())
                    .userEmail(tokenMapping.getUserEmail())
                    .build();
            // mariadb database에 refreshToken 저장
            tokenRepository.save(token);

            // 로그인 할때, 액세스토큰과 , 리프레시토큰을 반환
            AuthResponse authResponse = AuthResponse.builder().accessToken(tokenMapping.getAccessToken()).refreshToken(token.getRefreshToken()).build();
            return authResponse;




    }

    public MessageResponse signup(SignUpRequest signUpRequest){
//        DefaultAssert.isTrue(!userRepository.existsByEmail(signUpRequest.getEmail()), "해당 이메일이 존재하지 않습니다.");

        User user = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .provider(1)
                .roles(1)
                .activate(false)
                .build();

        userRepository.save(user);

//        URI location = ServletUriComponentsBuilder
//                .fromCurrentContextPath().path("/auth/")
//                .buildAndExpand(user.getId()).toUri();
//        ApiResponse apiResponse = ApiResponse.builder().check(true).information(Message.builder().message("회원가입에 성공하였습니다.").build()).build();
        MessageResponse messageResponse = MessageResponse.builder().result("success").msg("회원가입에 성공하였습니다.").build();
//        return ResponseEntity.created(location).body(apiResponse);
        // 여기 create 해야 할텐데
        return messageResponse;
    }
    public void kakaoAuthorize(HttpServletResponse response) throws IOException {
        String url = "https://kauth.kakao.com/oauth/authorize?client_id="+CLIENT_ID +
                                "&redirect_uri=https://churros.site/api/user/kakao/callback&response_type=code";
        // step1 :  1번 oauth/authorize 보내  , 2번은 Kakao Auth Server , 3번은 Client , 4번은 KakaoAuthServer ,5번은 Client
        response.sendRedirect(url);
    }

    public void kakaoCallBack(String code, HttpServletResponse response) throws IOException{
        JSONObject accessTokenKakao =  getKakaoTokenFeign.getKakaoToken("authorization_code",CLIENT_ID,
                "https://churros.site/api/user/kakao/callback",code);


        JSONObject resp = getKakaoInfoFeign.getKakaoInfo((String)accessTokenKakao.get("token_type")+" "+(String)accessTokenKakao.get("access_token"));
        //  회원 가입
        Optional<User> user = this.kakaoSignup(resp);

        // 토큰 생성
        String[] tokens = createJWTToken(user);

        response.sendRedirect("https://churros.site/kakao/handler?access-token="+tokens[0]+"&refresh-token="+tokens[1]);
    }

    public MyPageResponse myPage(Long userIdx){
        Optional<User> user = userRepository.findById(userIdx);
        log.info(user.toString());



        MyPageResponse myPageResponse = MyPageResponse.builder().result("success").name(user.get().getName())
                .email(user.get().getEmail()).provider(user.get().getProvider()).imageUrl(user.get().getImageUrl()).activate(user.get().getActivate()).build();
        return myPageResponse;
    }

    public MessageResponse activate(Long userIdx){
        Optional<User> userOptional = userRepository.findById(userIdx);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            if(user.getActivate()){
                user.setActivate(false);
                userRepository.save(user);
                MessageResponse messageResponse = MessageResponse.builder().result("success").msg("비활성화 되었습니다.").build();
                return messageResponse;
            }else{
                user.setActivate(true);
                userRepository.save(user);
                MessageResponse messageResponse = MessageResponse.builder().result("success").msg("활성화 되었습니다.").build();
                return messageResponse;
            }

        }

        MessageResponse messageResponse = MessageResponse.builder().result("success").msg("아이디가 존재하지 않습니다.").build();


      return messageResponse;
    }

    @Override
    public HashMap<String, String> refresh(String refreshToken) {
        // 여기서 이제 데이터베이스 갔다와야함
        Optional<Token> token = tokenRepository.findByRefreshToken(refreshToken);
        String result = "";
        if(token.isPresent()){
            String email = token.get().getUserEmail();
            Optional<User> user = userRepository.findByEmail(email);
            Long userIdx = user.get().getId();
            result = createJWTTokenFromRefreshToken(userIdx);

        }
        String accessToken = result;
        return new HashMap<String,String>(){{
            put("access-token", accessToken);
        }};
    }

    @Override
    public MessageResponse logout(Long userIdx) {

        Optional<User> user = userRepository.findById(userIdx);
        Optional<Token> optionalToken = tokenRepository.findByUserEmail(user.get().getEmail());
        if(optionalToken.isPresent()){
            Token token = Token.builder().userEmail(optionalToken.get().getUserEmail()).refreshToken(optionalToken.get().getRefreshToken())
                    .createdDate(optionalToken.get().getCreatedDate()).expireDate(optionalToken.get().getExpireDate()).build();
            tokenRepository.delete(token);
        }

        MessageResponse messageResponse = MessageResponse.builder().result("success").msg("로그아웃 했습니다.").build();
        return messageResponse;
    }

    @Override
    public MessageResponse deleteUser(Long userIdx) {
        Optional<User> user = userRepository.findById(userIdx);
        if(user.isPresent()){
        userRepository.delete(user.get());
            MessageResponse messageResponse = MessageResponse.builder().result("success").msg("회원탈퇴 되었습니다.").build();
            return messageResponse;
        }else{
            MessageResponse messageResponse = MessageResponse.builder().result("success").msg("회원이 없습니다.").build();
            return messageResponse;
        }

    }


    public Optional<User> kakaoSignup(JSONObject resp){

        Map<String, Object> map;
        map = (Map<String, Object>) resp.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) map.get("profile");
        String name = (String) profile.get("nickname");
        String image_url = (String) profile.get("profile_image_url");
        String email = "";
        if(map.get("email") == null){
            email = name + "@churros.com";
        }else{
        email = (String) map.get("email");

        }
        Optional<User> testUser = userRepository.findByEmail(email);
        MessageResponse messageResponse;
        if(testUser.isEmpty()){
            User user = User.builder()
                    .name(name)
                    .email(email)
                    .imageUrl(image_url)
                    .provider(3)
                    .roles(1)
                    .activate(false)
                    .build();

            userRepository.save(user);
            testUser = userRepository.findByEmail(email);

        }else{

        }

        return testUser;
    }

    private String createJWTTokenFromRefreshToken(Long userIdx){
        Date now = new Date();
        // 좀 찍어보자
        Date accessTokenExpiresIn = new Date(now.getTime() + 20000000);
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        // sub 엔 유저 id, iat엔 시작시점, exp 엔 만료되는 시점
        String accessToken = Jwts.builder()
                .setSubject(Long.toString(userIdx))
                .setIssuedAt(now)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();


        return accessToken;
    }
    private String[] createJWTToken(Optional<User> user){
        Date now = new Date();
        // 좀 찍어보자
        Date accessTokenExpiresIn = new Date(now.getTime() + 20000000);
        Date refreshTokenExpiresIn = new Date(now.getTime() + 600000000);

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        // sub 엔 유저 id, iat엔 시작시점, exp 엔 만료되는 시점
        String accessToken = Jwts.builder()
                .setSubject(Long.toString(user.get().getId()))
                .setIssuedAt(now)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
        String[] tokens = new String[2];
        tokens[0] = accessToken;
        tokens[1] = refreshToken;
        // 리프레시토큰 저장
        Token token = Token.builder().refreshToken(tokens[1]).userEmail(user.get().getEmail()).createdDate(now).expireDate(refreshTokenExpiresIn).build();
        tokenRepository.save(token);
        return tokens;
    }



}
