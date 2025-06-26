package com.koreait.SpringSecurityStudy.service;

import com.koreait.SpringSecurityStudy.dto.ApiRespDto;
import com.koreait.SpringSecurityStudy.dto.SignUpReqDto;
import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApiRespDto<?> addUser(SignUpReqDto signUpReqDto){
        //toEntity를 통해 전달 객체에서 엔티티로 변환 시 bCryptPasswordEncoder를 사용해 비밀번호 암호화
        int result = userRepository.addUser(signUpReqDto.toEntity(bCryptPasswordEncoder));

        //이메일 중복 확인

        return new ApiRespDto<>("success", "회원가입이 완료되었습니다.", result);
    }

//    public ApiRespDto<?> getUserByUserId(Integer userId){
//        Optional<User> user = userRepository.getUserByUserId(userId);
//        if (user.isEmpty()){
//            return new ApiRespDto<>("null", )
//        }
//    }
}
