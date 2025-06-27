package com.koreait.SpringSecurityStudy.mapper;

import com.koreait.SpringSecurityStudy.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    void addUser(User user);
    Optional<User> getUserByUserId(Integer userId);
    Optional<User> getUserByUserName(String userName);
}
