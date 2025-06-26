package com.koreait.SpringSecurityStudy.security.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
/*
 Custom Filter 로직 중 실제 DB에서 사용자 인증 정보를 가져오도록 구현한 부분(UserDetailService)에서
 사용자 정보를 전달받아 UserDetails 객체를 생성
 => SecurityContextHolder에 저장가능한 형태의 객체
 */
public class PrincipalUser implements UserDetails {
    private Integer userId;
    private String userName;
    private String password;
    private String userEmail;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
