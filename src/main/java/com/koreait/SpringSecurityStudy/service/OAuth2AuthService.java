package com.koreait.SpringSecurityStudy.service;

import com.koreait.SpringSecurityStudy.dto.ApiRespDto;
import com.koreait.SpringSecurityStudy.dto.OAuth2MergerReqDto;
import com.koreait.SpringSecurityStudy.dto.OAuth2SignupReqDto;
import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.entity.UserRole;
import com.koreait.SpringSecurityStudy.repository.OAuth2UserRepository;
import com.koreait.SpringSecurityStudy.repository.UserRepository;
import com.koreait.SpringSecurityStudy.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuth2AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //OAuth2 회원가입
    public ApiRespDto<?> signup(OAuth2SignupReqDto oAuth2SignupReqDto){
        //회원가입 요청된 이메일로 가입된 사용자가 있는지 확인
        Optional<User> userCheckDB = userRepository.getUserByUserEmail(oAuth2SignupReqDto.getUserEmail());
        if(userCheckDB.isPresent()){
            return new ApiRespDto<>("failed", "이미 가입된 이메일입니다.", null);
        }

        //DB에 사용자 정보 추가
        Optional<User> user = userRepository.addUser(
                oAuth2SignupReqDto.toEntityUser(bCryptPasswordEncoder)); //사용자

        UserRole userRole = UserRole.builder()
                    .userId(user.get().getUserId())
                    .roleId(3) //임시 사용자로 지정
                    .build();
        userRoleRepository.insert(userRole); //권한

        int result = oAuth2UserRepository.insertOAuth2User(
                oAuth2SignupReqDto.toEntityOAuth2User(user.get().getUserId())); //OAuth2 연동
        if(result == 0){
            return new ApiRespDto<>("failed", "오류가 발생했습니다.", result);
        }

        return new ApiRespDto<>("success", "OAuth2 회원가입이 완료되었습니다.", result);
    }

    //OAuth2 연동 (회원가입된 사용자의 연동 요청)
    public ApiRespDto<?> merge(OAuth2MergerReqDto oAuth2MergerReqDto){
        //연동 요청된 사용자 이름으로 가입된 사용자가 있는지 확인
        Optional<User> user = userRepository.getUserByUserName(oAuth2MergerReqDto.getUserName());
        if(user.isEmpty()){
            return new ApiRespDto<>("failed", "사용자 정보를 확인하세요.", null);
        }

        //비밀번호가 일치하는지 확인
        if(!bCryptPasswordEncoder.matches(oAuth2MergerReqDto.getPassword(), user.get().getPassword())){
            return new ApiRespDto<>("failed", "사용자 정보를 확인하세요.", null);
        }

        int result = oAuth2UserRepository.insertOAuth2User(oAuth2MergerReqDto.toEntityOAuth2User(user.get().getUserId()));
        if(result == 0){
            return new ApiRespDto<>("failed", "오류가 발생했습니다.", null);
        }

        return new ApiRespDto<>("success", "OAuth2 연동이 완료되었습니다.", null);
    }
}
