package com.koreait.SpringSecurityStudy.security.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.koreait.SpringSecurityStudy.entity.UserRole;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


/*
 Custom Filter 로직 중 실제 DB에서 사용자 인증 정보를 가져오도록 구현한 부분(UserDetailService)에서
 사용자 정보를 전달받아 UserDetails 객체를 생성
 => SecurityContextHolder에 저장가능한 형태의 객체
 */
@Data
@Builder
public class PrincipalUser implements UserDetails {
    private Integer userId;
    private String userName;
    /*
    @JsonIgnore
    SecurityContextHolder.getContext().getAuthentication()가 자동 매핑될 때
    비밀번호를 반환하면 안되기 때문에 해당 필드(password)를 무시하도록 설정
     */
    @JsonIgnore
    private String password;
    private String userEmail;

    //권한
    private List<UserRole> userRoles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRoles.stream()
                //리스트의 각 권한 요소들로 부터 권한명을 삽입한 새로운 객체를 생성
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public String getUsername() {
        return this.userName;
    }
}
