package com.koreait.SpringSecurityStudy.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/*
   DefaultOAuth2UserService
   - Spring Security에서 기본으로 제공하는 OAuth2UserService 표준 OAuth 2.0 공급자를 지원하는 구현
   - 해당 클래스를 상속받아 OAuth2UserService 커스텀
   - 인증 흐름
     OAuth2LoginAuthenticationFilter
     → OAuth2LoginAuthenticationProvider
     → 내부적으로 OAuth2UserService.loadUser() 호출
     → OAuth2PrincipalUserService 에서 오버라이딩한 loadUser() 사용
     → 커스텀한 로직에서 파싱된 DefaultOAuth2User가 Spring Security 내부에서 OAuth2AuthenticationToken에 담김
     → 최종적으로 SecurityContextHolder에 DefaultOAuth2User를 포함한 토큰 저장
     → OAuth2SuccessHandler로 이동
   *PrincipalUser : 인증된 사용자
   **해당 클래스에서는 외부 인증만 완료된 상태 (서버 내부의 인증을 거치지 않은 상태)
 */
@Service
public class OAuth2PrincipalUserService extends DefaultOAuth2UserService {
    /*
    loadUser 오버라이딩
    - OAuth2로 Access Token 발급 후 호출되는 메서드
    - 파라미터 userRequest로 토큰 정보가 들어옴
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        /*
        super.loadUser()
        -> Spring Security가 OAuth2 공급자(provider)에게 Access Token 으로 사용자 정보를 요청
        -> 사용자 정보(JSON)를 파싱한 객체를 반환받음
         */
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //사용자 정보(Map 형태) 저장
        Map<String, Object> attributes = oAuth2User.getAttributes();

        //로그인한 사용자의 식별자(id), 이메일 임시 저장
        String email = null;
        String id = null; //공급처에서 발행한 고유 식별자

        //어떤 OAuth2 공급자인지 확인 (예, registrationId='google')
        String provider = userRequest.getClientRegistration().getRegistrationId();
        //공급자 종류에 따라 사용자 정보 파싱 방식이 다르므로 필요한 정보를 빼내기 위해 분기 처리
        switch (provider){
            case "google" :
                id = attributes.get("sub").toString(); //키:sub 값:식별자
                email = attributes.get("email").toString(); //키:email 값:이메일
                break;
            default:
                break;
        }

        //필요한 정보(식별자, 공급자, 이메일)만 골라서 새롭게 attributes 구성
        Map<String, Object> newAttributes = Map.of(
                "id", id,
                "provider", provider,
                "email", email
        );

        //권한 설정(임시 권한 부여)
        //실제 권한은 OAuth2SuccessHandler 에서 판단
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_TEMPORARY"));

        //Spring Security 가 사용할 OAuth2User 객체를 생성해서 반환
        //권한, 속성, 아이디(principal.getName() 가져올 때 키로 사용)
        return new DefaultOAuth2User(authorities, newAttributes, "id");
    }
}
