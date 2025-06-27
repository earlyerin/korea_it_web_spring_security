package com.koreait.SpringSecurityStudy.repository;

import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {
    @Autowired
    private UserMapper userMapper;

    public Optional<User> addUser(User user){
        try{
            userMapper.addUser(user); //DB INSERT -> Set userId
        }catch (DuplicateKeyException e){ //기본키 무결성 위배(중복 등)로 인한 예외 처리
            return Optional.empty();
        }
        return Optional.of(user);
    }

    public Optional<User> getUserByUserId(Integer userId){
        return userMapper.getUserByUserId(userId);
    }

    public Optional<User> getUserByUserName(String userName){
        return userMapper.getUserByUserName(userName);
    }

    public int updateEmail(User user){
        return userMapper.updateEmail(user);
    }

    public int updatePassword(Integer userId, String password){
        return userMapper.updatePassword(userId, password);
    }
}
