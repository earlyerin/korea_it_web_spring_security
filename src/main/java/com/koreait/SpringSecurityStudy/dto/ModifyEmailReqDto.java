package com.koreait.SpringSecurityStudy.dto;

import com.koreait.SpringSecurityStudy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModifyEmailReqDto {
    private String userEmail;

    public User toEntity(Integer userId){
        return User.builder()
                .userId(userId)
                .userEmail(this.userEmail)
                .build();
    }
}
