package com.koreait.SpringSecurityStudy.dto;

import com.koreait.SpringSecurityStudy.entity.OAuth2User;
import lombok.Data;

@Data
public class OAuth2MergerReqDto {
    private String userName;
    private String password;
    private String provider;
    private String providerUserId;

    public OAuth2User toEntityOAuth2User(Integer userId){
        return OAuth2User.builder()
                .userId(userId)
                .provider(this.provider)
                .providerUserId(providerUserId)
                .build();
    }
}
