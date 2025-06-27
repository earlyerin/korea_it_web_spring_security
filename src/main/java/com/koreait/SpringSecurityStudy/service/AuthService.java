package com.koreait.SpringSecurityStudy.service;

import com.koreait.SpringSecurityStudy.dto.*;
import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.entity.UserRole;
import com.koreait.SpringSecurityStudy.repository.UserRepository;
import com.koreait.SpringSecurityStudy.repository.UserRoleRepository;
import com.koreait.SpringSecurityStudy.security.jwt.JwtUtil;
import com.koreait.SpringSecurityStudy.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApiRespDto<?> addUser(SignUpReqDto signUpReqDto){
        //DTO에서 Entity로 변환 시 bCryptPasswordEncoder를 사용해 비밀번호 암호화하여 전달
        Optional<User> user = userRepository.addUser(signUpReqDto.toEntity(bCryptPasswordEncoder));
        //회원이름 중복 확인

        //권한 부여
        UserRole userRole = UserRole.builder()
                .userId(user.get().getUserId())
                .roleId(3) //처음 회원가입 시 임시사용자(3)로 지정
                .build();
         Optional<UserRole> optionalUserRole = userRoleRepository.insert(userRole); //DB에 추가
        if(optionalUserRole.isEmpty()){
            return new ApiRespDto<>("failed", "권한 부여에 실패했습니다.",null);
        }
        return new ApiRespDto<>("success", "회원가입이 완료되었습니다.", user);
    }

    public ApiRespDto<?> signin(SignInReqDto signInReqDto){
        Optional<User> optionalUser = userRepository.getUserByUserName(signInReqDto.getUserName());
        //회원의 이름이 일치하는지 확인
        if(optionalUser.isEmpty()){
            return new ApiRespDto<>("failed", "사용자 정보를 확인해주세요.", null);
        }
        User user = optionalUser.get(); //DB 데이터
        //비밀번호가 일치하는지 확인
        if(!bCryptPasswordEncoder.matches(signInReqDto.getPassword(), user.getPassword())){
            return new ApiRespDto<>("failed", "사용자 정보를 확인해주세요.", null);
        }
        System.out.println("로그인 성공");
        //토큰을 생성 (반환된 토큰은 Local Storage에 저장)
        String token = jwtUtil.generateAccessToken(String.valueOf(user.getUserId()));
        return new ApiRespDto<>("success", "로그인이 완료되었습니다.", token);
    }

    public ApiRespDto<?> modifyEmail(Integer userId, ModifyEmailReqDto modifyEmailReqDto){
        User user = modifyEmailReqDto.toEntity(userId);
        int result = userRepository.updateEmail(user);
        return new ApiRespDto<>("success", "이메일을 변경하였습니다.", result);
    }

    public ApiRespDto<?> modifyPassword
            (ModifyPasswordReqDto modifyPasswordReqDto, PrincipalUser principalUser){
        //현재 비밀번호 확인
        if(!bCryptPasswordEncoder.matches(modifyPasswordReqDto.getOldPassword(), principalUser.getPassword())){
            return new ApiRespDto<>("failed", "사용자 정보를 확인하세요.", null);
        }
        //새로운 비밀번호로 두번 입력받은 필드끼리 비교
        if(!modifyPasswordReqDto.getNewPassword().equals(modifyPasswordReqDto.getNewPasswordCheck())){
            return new ApiRespDto<>("failed", "새 비밀번호가 일치하지 않습니다.", null);
        }
        int result = userRepository.updatePassword(principalUser.getUserId(),
                bCryptPasswordEncoder.encode(modifyPasswordReqDto.getNewPassword())); //비밀번호 암호화
        return new ApiRespDto<>("success", "비밀번호를 변경하였습니다.", result);
    }


}
