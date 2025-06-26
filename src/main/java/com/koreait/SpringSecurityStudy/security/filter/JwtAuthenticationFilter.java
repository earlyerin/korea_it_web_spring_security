package com.koreait.SpringSecurityStudy.security.filter;

import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.repository.UserRepository;
import com.koreait.SpringSecurityStudy.security.jwt.JwtUtil;
import com.koreait.SpringSecurityStudy.security.model.PrincipalUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/*
 AuthenticationFilter
 요청을 가로채고, 가로챈 정보를 통해 유저 자격을 기반으로 인증 토큰 생성
 */
@Component //Bean 등록
public class JwtAuthenticationFilter implements Filter { //Custom AuthenticationFilter
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest; //받은 요청에 대한 객체

        //요청이 아래의 메서드가 아닐 경우 다음 필터로 이동
        List<String> methods = List.of("POST", "PUT", "GET", "PATCH", "DELETE");
        if(!methods.contains(request.getMethod())){ //getMethod() : HTTP 요청 메소드를 반환
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        //토큰 인증
        //AuthenticationManager가 등록된 AuthenticationProvider들을 조회하며 인증을 요구하는 것과 같은 역할 구현
        String authorization = request.getHeader("Authorization"); //HTTP 요청 헤더 중 "Authorization"의 값을 반환
        System.out.println("Bearer Token : " + authorization);
        if(jwtUtil.isBearer(authorization)){ //유효한 토큰이면
            String accessToken = jwtUtil.removeBearer(authorization); //반환된 토큰 저장
            try {
                Claims claims = jwtUtil.getClaim(accessToken); //토큰에서 Claims를 추출하고 서명검증(예외처리 필요)

        //실제 DB에서 사용자 정보를 가져오는 UserDetailService의 역할 구현
                String id = claims.getId(); //고유 식별자 반환
                Integer userId = Integer.parseInt(id);
                Optional<User> optionalUser = userRepository.getUserByUserId(userId); //고유 식별자로 DB 조회
        //UserDetails에서의 객체생성 역할 구현
                //DB에서 조회 후 반환된 User 객체가 존재하면 principalUser 객체로 변환
                optionalUser.ifPresentOrElse((user) -> {
                    PrincipalUser principalUser = PrincipalUser.builder()
                            .userId(user.getUserId())
                            .userName(user.getUserName())
                            .password(user.getPassword())
                            .userEmail(user.getUserEmail())
                            .build();
        //UsernamePasswordAuthenticationToken 생성 및 SecurityContext에 저장 구현
                    Authentication authentication = new UsernamePasswordAuthenticationToken
                            (principalUser, "", principalUser.getAuthorities());
                             //인증된 객체, 비밀번호(이미 인증되었으므로 생략), 권한들

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    //Security Context에 인증된 객체를 저장하면 이후 요청(Filter를 거칠 때)은 인증된 사용자로 간주
                    System.out.println("인증 완료 : " + authentication.getName());
                },
                //DB에서 조회 후 반환된 User 객체가 존재하지 않으면 예외 발생
                        () -> {
                            throw new AuthenticationServiceException("인증 실패");
                        });
            }catch (RuntimeException e){
                e.printStackTrace();
            }
        }
        filterChain.doFilter(servletRequest, servletResponse); //다음 필터로 이동
    }
}
