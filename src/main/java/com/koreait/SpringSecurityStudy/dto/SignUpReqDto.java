package com.koreait.SpringSecurityStudy.dto;

import com.koreait.SpringSecurityStudy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@AllArgsConstructor
public class SignUpReqDto {
    private String userName;
    private String password;
    private String userEmail;

    public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder){
        return User.builder()
                .userName(this.userName)
                .password(bCryptPasswordEncoder.encode(this.password)) //비밀번호 암호화
                .userEmail(this.userEmail)
                .build();
    }
}
