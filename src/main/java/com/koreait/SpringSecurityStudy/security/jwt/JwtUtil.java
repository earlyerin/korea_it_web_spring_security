package com.koreait.SpringSecurityStudy.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.swing.plaf.PanelUI;
import java.security.Key;
import java.util.Date;

@Component //Bean 등록
public class JwtUtil { //Custom Filter에 사용할 메서드 구현
    private final Key KEY;

    //생성자로 secret key값 초기화(application.properties에 명시된 값)
    public JwtUtil(@Value("${jwt.secret}") String secret){
        KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    //JWT 토큰 생성
    public String generateAccessToken(String id){
        return Jwts.builder()
                .subject("AccessToken") //토큰의 용도를 설명하는 식별자 역할
                .id(id) //토큰의 고유한 식별자 부여(userId) => 사용자 무효화나 조회에 사용
                .expiration(new Date(new Date().getTime() + (1000L * 60L * 60L * 24L * 30L)))
                //new Date().getTime() + (1000L * 60L * 60L * 24L * 30L) : 현재로 부터 30일 뒤 => 토큰의 만료기간 지정
                .signWith(KEY) //토큰에 서명(signature)을 적용
                .compact(); //설정한 내용을 바탕으로 최종 문자열 형태의 JWT 생성
        /*
        eyJhbGciOiJIUzUxMiJ9. (Header)
        eyJzdWIiOiJBY2Nlc3NUb2tlbiIsImp0aSI6IjEiLCJleHAiOjE3NTM1NjUzODl9. (Payload)
        q5Ar6xTkrczyx9v_T0ZxNNWNUePQQj-_HTJvtVGvaGPnA0rJnW_urSzsh86HdEjvt7N5CJ75yRtSC4_EP5Vdew (Signature)
        [Header]
        {
            "alg": "HS512"
        }
        [Payload]
        {
            "sub": "AccessToken",
            "jti": "1",
            "exp": 1753565389
        }
         */
    }

    //이메일 인증 토큰(이메일 인증 시 한 번만 사용)
    public String generateMailVerifyToken(String id){
        return Jwts.builder()
                .subject("VerifyToken") //검증 토큰
                .id(id)
                .expiration(new Date(new Date().getTime() + (1000L * 60L * 3L))) //토큰 만료 기간 : 3분
                .signWith(KEY)
                .compact();
    }

    //유효한 토큰인지 확인
    public boolean isBearer(String token) throws NullPointerException{
        if(token == null || !token.startsWith("Bearer ")){
            return false;
        }
        return true;
    }

    //접두사 Bearer 제거 후 Token만 반환
    public String removeBearer(String bearerToken){
        return bearerToken.replaceFirst("Bearer ", "");
        //Bearer 1q2w3e4r;lajfafasdau668uk*^%&(Token) => 1q2w3e4r;lajfafasdau668uk*^%&(Token)
    }

    //Payload 영역의 Claims 정보를 반환
    public Claims getClaim(String token) throws JwtException { //JwtException : 토큰의 위변조나 만료로 발생하는 예외
        /*
         Jwts.parser() Method
         - JwtParserBuilder 객체를 반환 (JwtParserBuilder jwtParserBuilder)
         - JWT 파서의 구성을 만들 때, 즉 parser 설정 작업 시 빌더로 메서드 체이닝 가능
         */
        JwtParser jwtParser = Jwts.parser()
                .verifyWith((SecretKey)KEY) //토큰의 서명을 검증하기 위해 secret key 설정
                .build(); //설정이 완료된 파서를 빌드해서 최종 JwtParser 객체 생성
        return jwtParser.parseSignedClaims(token).getPayload(); //순수 Claims JWT를 파싱
        //
    }
}
