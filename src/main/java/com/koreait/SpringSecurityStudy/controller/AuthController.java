package com.koreait.SpringSecurityStudy.controller;

import com.koreait.SpringSecurityStudy.dto.SignInReqDto;
import com.koreait.SpringSecurityStudy.dto.SignUpReqDto;
import com.koreait.SpringSecurityStudy.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @GetMapping("/test")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok("test");
    }

    @PostMapping("/signup") //회원가입
    public ResponseEntity<?> signup(@RequestBody SignUpReqDto signUpReqDto){
        return ResponseEntity.ok(authService.addUser(signUpReqDto));
    }

    @PostMapping("/signin") //로그인
    public ResponseEntity<?> signin(@RequestBody SignInReqDto signInReqDto){
        return ResponseEntity.ok(authService.signin(signInReqDto));
    }

    @GetMapping("/principal") //회원정보
    public ResponseEntity<?> getPrincipal() {
        //필터를 지나며 SecurityContextHolder에 저장된 Principal 객체 반환
        return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication());
        /*
        {
          "authorities": [],
            "details": null,
            "authenticated": true, (인증됨)
            "principal": {
                "userId": 1,
                "userEmail": "gildong@naver.com",
                "enabled": true, (계정 활성화)
                "authorities": [],
                "accountNonLocked": true, (계정 잠금)
                "username": "gildong",
                "credentialsNonExpired": true, (비밀번호 만료 기간)
                "accountNonExpired": true (계정 만료 기간)
            },
            "credentials": null,
            "name": "gildong"
        }
         */
    }
}
