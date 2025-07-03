package com.koreait.SpringSecurityStudy.mapper;

import com.koreait.SpringSecurityStudy.entity.OAuth2User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public interface OAuth2UserMapper {
    OAuth2User getOAuth2UserByProviderAndProviderUserId(String provider, String providerUserId);
}
