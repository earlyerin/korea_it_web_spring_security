package com.koreait.SpringSecurityStudy.controller;

import com.koreait.SpringSecurityStudy.dto.SendMailReqDto;
import com.koreait.SpringSecurityStudy.security.model.PrincipalUser;
import com.koreait.SpringSecurityStudy.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller //JSP
@RequestMapping("/mail")
public class MailController {
    @Autowired
    private MailService mailService;

    @PostMapping("/send") //사용자 이메일 인증을 위한 메일 전송
    public ResponseEntity<?> sendMail(@RequestBody SendMailReqDto sendMailReqDto,
                                      @AuthenticationPrincipal PrincipalUser principalUser){
        return ResponseEntity.ok(mailService.sendMail(sendMailReqDto, principalUser));

    }

}
