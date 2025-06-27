package com.koreait.SpringSecurityStudy.repository;


import com.koreait.SpringSecurityStudy.mapper.OAuth2UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Repository;

@Repository
public class OAuth2UserRepository {
    @Autowired
    private OAuth2UserMapper oAuth2UserMapper;

    public OAuth2User getOAuth2UserByProviderAndProviderUserId(String provider, String providerUserId){
        return oAuth2UserMapper.getOAuh2UserByProviderAndProviderUserId(provider, providerUserId);
    }
}
