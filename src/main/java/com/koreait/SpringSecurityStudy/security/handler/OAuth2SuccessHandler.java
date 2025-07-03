package com.koreait.SpringSecurityStudy.security.handler;

import com.koreait.SpringSecurityStudy.entity.OAuth2User;
import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.repository.OAuth2UserRepository;
import com.koreait.SpringSecurityStudy.repository.UserRepository;
import com.koreait.SpringSecurityStudy.security.jwt.JwtUtil;
import com.koreait.SpringSecurityStudy.security.model.PrincipalUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/*
 AuthenticationSuccessHandler
 - Spring Security가 외부로 부터 인증된 사용자의 정보를 DefaultOAuth2UserService 객체에 담아서
   SecurityContextHolder에 저장 -> AuthenticationSuccessHandler 호출
 - OAuth2SuccessHandler에서 AuthenticationSuccessHandler를 커스텀하여 로그인 시도 후 로직 구현
    - 내부적으로 사용자가 DB에 존재하는지 확인 -> JWT 토큰을 반환
    - DB에 연동되어 있지 않은 경우 -> 회원가입 또는 연동 페이지로 이동 (프론트에서 구현)
      -> 다시 OAuth2PrincipalUserService에서 부터 인증 시작
 - 최종적으로 JWT을 반환하면 내부적으로 인증된 사용자(로그인 완료)
 */
@Component //Bean 등록
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        //SecurityContextHolder에 저장된 OAuth2User 정보 가져오기
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String provider = defaultOAuth2User.getAttribute("provider");
        String providerUserId = defaultOAuth2User.getAttribute("id");
        String email = defaultOAuth2User.getAttribute("email");

        //공급자와 식별자를 사용하여 이미 연동되어있는 사용자인지 DB 조회
        OAuth2User oAuth2User = oAuth2UserRepository
                .getOAuth2UserByProviderAndProviderUserId(provider, providerUserId);
        //DB 연동되어 있지 않은 경우
        if(oAuth2User == null){
            //프론트 서버(React)로 공급자, 식별자, 이메일 반환 => 해당 웹페이지에서 회원가입 또는 연동
            response.sendRedirect("http://localhost:3000/auth/oauth2?provider=" + provider
                    + "&providerUserId=" + providerUserId
                    + "&email=" + email);
        }
        //DB 연동되어 있는 경우
        Optional<User> user = userRepository.getUserByUserId(oAuth2User.getUserId());

        //OAuth2 로그인을 통해 회원가입이나 연동을 진행한 경우
        String accessToken = null;
        if (user.isPresent()){
            //JWT 토큰 생성
            accessToken = jwtUtil.generateAccessToken(user.get().getUserId().toString());
        }

        //최종적으로 토큰을 쿼리파라미터로 프론트에 전달
        response.sendRedirect("http://localhost:3000/auth/oauth2/signin?accessToken="
                + accessToken);

    }
}
