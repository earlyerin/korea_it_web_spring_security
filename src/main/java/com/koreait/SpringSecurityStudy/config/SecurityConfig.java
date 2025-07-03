package com.koreait.SpringSecurityStudy.config;

import com.koreait.SpringSecurityStudy.security.filter.JwtAuthenticationFilter;
import com.koreait.SpringSecurityStudy.security.handler.OAuth2SuccessHandler;
import com.koreait.SpringSecurityStudy.service.OAuth2PrincipalUserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private OAuth2PrincipalUserService oAuth2PrincipalUserService;

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    /*
     BCryptPasswordEncoder
     - 비밀번호를 안전하게 암호화(해싱)하고, 검증하는 역할
     - 단방향 해시이므로 복호화 불가능
       따라서 클라이언트의 요청으로 들어온 비밀번호를 암호화하여 저장된 데이터와 비교
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

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
           (해당 프로젝트의 경우 요청마다 토큰을 사용하는 비상태 방식이므로 csrf를 비활성화한다.)
         */
        http.csrf(csrf -> csrf.disable()); //CSRF 보호 비활성화

        //SSR 방식의 로그인 비활성화
        http.formLogin(formLogin -> formLogin.disable());
        //SSR 방식의 로그아웃 비활성화
        http.logout(logout -> logout.disable());

        //HTTP 프로토콜 기본 로그인 방식 비활성화
        http.httpBasic(httpBasic -> httpBasic.disable());

        //커스텀 필터 호출(인증)
        //UsernamePasswordAuthenticationFilter로 이동 전에 jwtAuthenticationFilter로 이동
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        //비상태(Stateless) 방식
        http.sessionManagement(Session
                -> Session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //특정 요청 URL에 대한 권한 설정
        http.authorizeHttpRequests(auth -> {
            /*
            DB 권한명      URL 권한 설정
            ROLE_ADMIN    hasRole("ADMIN")
            ADMIN         hasAuthority("ADMIN")
             */
            //해당 경로의 경우 관리자 권한을 가진 사용자만 요청 가능
            auth.requestMatchers("/auth/test").hasRole("ADMIN");
            //해당 경로의 경우 인증 필요X
            auth.requestMatchers(
                    "/auth/signup",
                            "/auth/signin",
                            "/oauth2/**",
                            "/login/oauth2/**")
                    .permitAll();
            //해당 경로가 아닌 요청은 모두 인증 필요O
            auth.anyRequest().authenticated();
//            auth.anyRequest().permitAll(); 인증 없이 모든 요청가능
        });

        /*
        OAuth2 로그인에 대한 설정
        - userInfoEndPoint() : 사용자 정보를 요청하는 엔드포인트
        - userService() : OAuth2 공급자로 부터 사용자 정보를 받아와서 파싱할 방식 설정
        - successHandler() : 사용자 정보 파싱 후 호출할 핸들러 설정
        **Endpoint란, 클라이언트가 서버에 요청을 보내는 특정 URL 경로
        - 흐름
            Spring Filter 에서 OAuth2 요청을 감지
            → 해당 공급자의 로그인 페이지로 리디렉션
            → OAuth2 로그인 요청이 성공하면 아래 설정대로 이동
        **Redirection이란, 사용자가 요청한 URL 대신 다른 URL로 자동으로 이동하도록 설정하는 것
         */
        http.oauth2Login(oauth2
                -> oauth2.userInfoEndpoint(userInfo
                                            -> userInfo.userService(oAuth2PrincipalUserService))
                         .successHandler(oAuth2SuccessHandler)
        );
        /*
        GCP(Google Cloud Platform) : 클라이언트(앱, 내 서비스) 등록
        새 프로젝트 -> 대시보드 -> API 및 서비스 -> 사용자 인증 정보
        -> OAuth 2.0 클라이언트 ID -> 사용자 인증 정보 만들기
        -> OAuth 클라이언트 만들기(승인된 리디렉션 URI = http://localhost:8080/login/oauth2/code/google)
        **승인된 리디렉션 URI : 공급자가 accessToken을 발급한 뒤 리디렉션할 URI, 미리 등록된 주소로만 허용(보안)
           applicaion.properties의 spring.security.oauth2.client.registration."google" 로 매핑
        **scope : 요청할 정보 명시

        Oauth2 공급자로 부터 권한을 위임 받기 위해 applicaion.properties에 클라이언트 정보 명시
        -> 클라이언트 ID, 클라이언트 보안 비밀번호, scope 설정 작성 (내 서버에 대한 증명, 인증 정보)

        localhost:8080/oauth2/authorization/google => Security에서 정해놓은 경로로 OAuth2 로그인 확인
         */

        return http.build();
    }
}
