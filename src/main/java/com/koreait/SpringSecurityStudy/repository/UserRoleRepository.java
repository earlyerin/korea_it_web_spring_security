package com.koreait.SpringSecurityStudy.repository;

import com.koreait.SpringSecurityStudy.entity.UserRole;
import com.koreait.SpringSecurityStudy.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public class UserRoleRepository {
    @Autowired
    private UserRoleMapper userRoleMapper;

    public Optional<UserRole> insert(UserRole userRole){
        return userRoleMapper.insert(userRole) < 1 ? //DB INSERT -> Set userRoleId
                Optional.empty() : Optional.of(userRole);
    }

    public Optional<UserRole> getUserRoleByUserIdAndRoleId(Integer userId, Integer roleId){
        return userRoleMapper.getUserRoleByUserIdAndRoleId(userId, roleId);
    }

    public int updateRoleId(Integer userId, Integer userRoleId){
        return userRoleMapper.updateRoleId(userId, userRoleId);
    }
}
