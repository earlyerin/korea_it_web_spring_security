package com.koreait.SpringSecurityStudy.service;

import com.koreait.SpringSecurityStudy.dto.ApiRespDto;
import com.koreait.SpringSecurityStudy.dto.SendMailReqDto;
import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.repository.UserRepository;
import com.koreait.SpringSecurityStudy.security.jwt.JwtUtil;
import com.koreait.SpringSecurityStudy.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MailService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender javaMailSender; //메일 전송을 위한 인터페이스

    //로그인, Token 인증이 된 사용자일 경우 이메일 인증 진행
    public ApiRespDto<?> sendMail(SendMailReqDto sendMailReqDto, PrincipalUser principalUser){
        //이메일이 동일한지 확인
        if(!principalUser.getUserEmail().equals(sendMailReqDto.getEmail())){
            return new ApiRespDto<>("failed", "잘못된 접근입니다.", null);
        }

        //해당 이메일의 사용자가 존재하는지 확인
        Optional<User> optionalUser = userRepository.getUserByUserEmail(sendMailReqDto.getEmail());
        if(optionalUser.isEmpty()){
            return new ApiRespDto<>("failed", "사용자 정보를 확인하세요.", null);
        }

        //해당 이메일의 사용자 권한이 임시사용자인지 확인
        User user = optionalUser.get();
        boolean hasTempRole = user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleId() == 3);
                //userRoles 요소 중 3이 하나라도 있으면 true
        if(!hasTempRole){
            return new ApiRespDto<>("failed", "이미 이메일 인증이 완료된 사용자입니다.", null);
        }

        //사용자에게 이메일 인증 토큰을 포함한 메일을 전송
        String token = jwtUtil.generateMailVerifyToken(user.getUserId().toString());
        /*
         SimpleMailMessage Class
         - 간단한 메일 메세지를 작성하기 위한 클래스
         - 텍스트 기반의 이메일 객체 생성
         */
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getUserEmail()); //수신자 이메일
        message.setSubject("이메일 인증 메일"); //메일 제목
        message.setText("해당 링크로 접속하여 인증을 완료하세요. → "
                + "http://localhost:8080/mail/verify?verifyToken=" + token); //본문 내용
        javaMailSender.send(message);
        //메일을 보내기 위해 application.properties에 smtp 설정 필요

        return new ApiRespDto<>("success", "인증 메일이 전송되었습니다. 메일을 확인하세요.", null);
    }
}
