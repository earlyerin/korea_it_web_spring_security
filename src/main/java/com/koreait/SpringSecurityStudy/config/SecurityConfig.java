package com.koreait.SpringSecurityStudy.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configurable //설정 파일 명시
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
        http.cors(Customizer.withDefaults()); //위의 CORS 설정을 Security에 적용
    }
}
