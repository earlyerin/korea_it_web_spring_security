package com.koreait.SpringSecurityStudy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration //설정 파일 명시
public class SecurityConfig { //도메인 간의 요청에 대한 보안설정
    /*
     CorsConfigurationSource
     - Spring Security 에서 CORS(Cross-Origin Resource Sharing)을 처리하기 위한 설정
     - CORS란, 브라우저가 보안상 다른 도메인의 리소스 요청을 제한하는 정책
     - 기본적으로 브라우저는 같은 출처(Same-Origin)만 허용한다.
     - 프론트엔드(React: localhost:3000) → 백엔드(Spring: localhost:8080)처럼
       출처(origin)가 다른 경우에도 요청할 수 있도록 설정 가능
     */
    @Bean //컨테이너에서 CORS 처리를 위해 Bean을 확인
    public CorsConfigurationSource configurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        //요청 시 도메인(사이트 주소)의 허용
        corsConfiguration.addAllowedOriginPattern(CorsConfiguration.ALL);

        //요청 시 Request, Response Header 정보에 대한 제약의 허용
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);

        //요청 시 메서드(GET, POST, PUT, DELETE, OPTION 등) 사용의 허용
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);

        //요청 URL(/user/get 등)에 대한 CORS 설정 적용을 위한 객체 생성
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //모든 URL(/**)에 대해 위에서 설정한 CORS 정책 적용
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        //요청 -> 서블릿 필터체인(SecurityFilterChain) -> MVC

        //위의 CORS 설정을 Security에 적용
        http.cors(Customizer.withDefaults());

        /*
         CSRF(Cross-Site Request Forgery)
         - 사용자가 의도하지 않은 요청을 공격자가 유도해서 서버에 전달하도록 하는 공격
         - 세션-쿠키 기반의 인증방식의 경우 csrf를 활성화해서 세션 정보가 노출되지 않도록 공격에 방어해야한다.
           (해당 프로젝트의 경우 요청마다 토큰을 사용하는 무상태 방식이므로 csrf를 비활성화한다.)
         */
        http.csrf(csrf -> csrf.disable()); //CSRF 보호 비활성화

        //SSR 방식의 로그인 비활성화
        http.formLogin(formLogin -> formLogin.disable());
        //SSR 방식의 로그아웃 비활성화
        http.logout(logout -> logout.disable());

        //HTTP 프로토콜 기본 로그인 방식 비활성화
        http.httpBasic(httpBasic -> httpBasic.disable());

        //특정 요청 URL에 대한 권한 설정
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/auth/**").permitAll(); //해당 경로의 경우 인증 필요X
            auth.anyRequest().authenticated(); //해당 경로가 아닌 요청은 모두 인증 필요O
//            auth.anyRequest().permitAll(); //인증 없이 모든 요청가능
        });

        //무상태(Stateless) 방식
        http.sessionManagement(Session
                -> Session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
