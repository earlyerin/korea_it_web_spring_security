package com.koreait.SpringSecurityStudy.dto;

import com.koreait.SpringSecurityStudy.entity.OAuth2User;
import com.koreait.SpringSecurityStudy.entity.User;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
public class OAuth2SignupReqDto {
    private String userName;
    private String password;
    private String userEmail;
    private String provider;
    private String providerUserId;

    //user_tb에 매핑하기 위해 User 엔티티로 변환하는 메서드
    public User toEntityUser(BCryptPasswordEncoder bCryptPasswordEncoder){
        return User.builder()
                .userName(this.userName)
                .password(bCryptPasswordEncoder.encode(this.password))
                .userEmail(this.userEmail)
                .build();
    }

    //oauth2_user_tb에 매핑(연동)하기 위해 OAuth2User 엔티티로 변환하는 메서드
    public OAuth2User toEntityOAuth2User(Integer userId){
        return OAuth2User.builder()
                .userId(userId)
                .provider(this.provider)
                .providerUserId(this.providerUserId)
                .build();
    }
}
