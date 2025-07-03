package com.koreait.SpringSecurityStudy.controller;

import com.koreait.SpringSecurityStudy.dto.SendMailReqDto;
import com.koreait.SpringSecurityStudy.security.model.PrincipalUser;
import com.koreait.SpringSecurityStudy.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Controller //JSP
@RequestMapping("/mail")
public class MailController {
    @Autowired
    private MailService mailService;

    @PostMapping("/send") //사용자 이메일 인증을 위한 메일 전송
    public ResponseEntity<?> sendMail(@RequestBody SendMailReqDto sendMailReqDto,
                                      @AuthenticationPrincipal PrincipalUser principalUser){
        return ResponseEntity.ok(mailService.sendMail(sendMailReqDto, principalUser));
        //OAuth2 로그인 후 받은 토큰을 사용해서 PostMan 메일 전송 요청으로 확인
    }

    @GetMapping("/verify")
    public String verify(Model model, //html 파일로 데이터를 전달할 매개변수
                         @RequestParam String verifyToken) {
        Map<String, Object> resultMap = mailService.verify(verifyToken);
        model.addAllAttributes(resultMap);
        return "result_page"; //resources.templates - html 파일 반환
    }




}
