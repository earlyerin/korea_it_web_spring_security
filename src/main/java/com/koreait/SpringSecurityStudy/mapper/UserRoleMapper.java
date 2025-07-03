package com.koreait.SpringSecurityStudy.mapper;

import com.koreait.SpringSecurityStudy.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserRoleMapper {
    //추가
    int insert(UserRole userRole);

    //조회(이메일 인증을 위해 권한 조회)
    Optional<UserRole> getUserRoleByUserIdAndRoleId(Integer userId, Integer roleId);

    //수정(이메일 인증 후 권한 변경)
    int updateRoleId(Integer userId, Integer userRoleId);
}
