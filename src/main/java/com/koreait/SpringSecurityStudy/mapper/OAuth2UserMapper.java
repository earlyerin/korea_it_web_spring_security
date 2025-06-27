package com.koreait.SpringSecurityStudy.mapper;

import com.koreait.SpringSecurityStudy.entity.Oauth2User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OAuth2UserMapper {
    Oauth2User getOAuh2UserByProviderAndProviderUserId(String provider, String providerUserId);
}
