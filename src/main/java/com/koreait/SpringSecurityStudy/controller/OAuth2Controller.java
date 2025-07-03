package com.koreait.SpringSecurityStudy.controller;

import com.koreait.SpringSecurityStudy.dto.OAuth2MergerReqDto;
import com.koreait.SpringSecurityStudy.dto.OAuth2SignupReqDto;
import com.koreait.SpringSecurityStudy.service.OAuth2AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {
    @Autowired
    private OAuth2AuthService oAuth2AuthService;

    @PostMapping("/signup") //OAuth2 로그인 후 서버에 가입이 되지 않은 사용자일 경우 해당 경로로 회원가입
    public ResponseEntity<?> signup(@RequestBody OAuth2SignupReqDto oAuth2SignupReqDto){
        return ResponseEntity.ok(oAuth2AuthService.signup(oAuth2SignupReqDto));
        /*
        회원가입 되지 않은 사용자가 로그인을 시도하면 반환되는 URL을 통해 PostMan으로 회원가입 요청 확인하기
        http://localhost:3000/auth/oauth2?provider=google&providerUserId=106365076620307610549&email=rin050301@gmail.com
        회원가입 후 브라우저에서 토큰 반환 확인하기
        http://localhost:3000/auth/oauth2/signin?accessToken=eyJhbGciOiJIUzUxMiJ9.
        eyJzdWIiOiJBY2Nlc3NUb2tlbiIsImp0aSI6IjIiLCJleHAiOjE3NTQxMTA3Mjh9.
        GrFHhC22mhgoSp11n5ZtoRPLKMMhdtEbKt14hGSkfX5syyIvyAfpGX1CVIe3Jm6UQVdxU8T-pmW5oeUVfmjBLA
         */
    }

    @PostMapping("/merge") //OAuth2 로그인 후 user로 회원가입은 했지만 oauth2 연동은 되지 않은 사용자일 경우 해당 경로로 연동
    public ResponseEntity<?> merge(@RequestBody OAuth2MergerReqDto oAuth2MergerReqDto){
        return ResponseEntity.ok(oAuth2AuthService.merge(oAuth2MergerReqDto));
    }


}
