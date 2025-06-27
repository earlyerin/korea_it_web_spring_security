package com.koreait.SpringSecurityStudy.controller;

import com.koreait.SpringSecurityStudy.dto.ModifyEmailReqDto;
import com.koreait.SpringSecurityStudy.dto.ModifyPasswordReqDto;
import com.koreait.SpringSecurityStudy.dto.SignInReqDto;
import com.koreait.SpringSecurityStudy.dto.SignUpReqDto;
import com.koreait.SpringSecurityStudy.security.model.PrincipalUser;
import com.koreait.SpringSecurityStudy.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/principal") //회원정보(토큰 인증 필요)
    public ResponseEntity<?> getPrincipal() {
        //필터를 지나며 SecurityContextHolder에 저장된 Principal 객체 반환
        return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication());
        /*
        {
            "authorities": [
                {
                    "authority": "ROLE_TEMPORARY"
                }
            ],
            "details": null,
            "authenticated": true, (인증됨)
            "principal": {
                "userId": 1,
                "userEmail": null,
                "userRoles": [ => 권한 리스트
                    {
                        "userRoleId": 1,
                        "userId": 1,
                        "roleId": 3,
                        "regDt": "2025-06-27T11:35:58",
                        "updDt": null,
                        "role": {
                            "roleId": 3,
                            "roleName": "ROLE_TEMPORARY",
                            "roleNameKor": "임시사용자"
                        }
                    }
                ],
                "enabled": true, (계정 활성화)
                "authorities": [ => 권한 리스트
                    {
                        "authority": "ROLE_TEMPORARY"
                    }
                ],
                "username": "gildong",
                "accountNonExpired": true, (계정 만료 기간)
                "credentialsNonExpired": true, (비밀번호 만료 기간)
                "accountNonLocked": true  (계정 잠금)
            },
            "credentials": null,
            "name": "gildong"
        }
         */
    }

    @PostMapping("/modify/email/{userId}") //이메일 변경(토큰 인증 필요)
    public ResponseEntity<?> modifyEmail(
            @PathVariable Integer userId, @RequestBody ModifyEmailReqDto modifyEmailReqDto){
        return ResponseEntity.ok(authService.modifyEmail(userId, modifyEmailReqDto));
    }

    @PostMapping("modify/password/{userId}")
    public ResponseEntity<?> modifyPassword(
            @PathVariable Integer userId,
            @RequestBody ModifyPasswordReqDto modifyPasswordReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser){
        /*
        @AuthenticationPrincipal
        SecurityContextHolder의 principal 객체를 가져오도록 명시
         */
        //userId 확인
        if(!principalUser.getUserId().equals(userId)){
            return ResponseEntity.badRequest().body("본인의 계정이 아닙니다.");
        }
        return ResponseEntity.ok(authService.modifyPassword(modifyPasswordReqDto,principalUser));
    }
}
