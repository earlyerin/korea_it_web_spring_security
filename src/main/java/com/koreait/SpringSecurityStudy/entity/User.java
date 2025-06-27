package com.koreait.SpringSecurityStudy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class User {
    private Integer userId;
    private String userName;
    @JsonIgnore //Json으로 자동 매핑 시 해당 필드 무시
    private String password;
    private String userEmail;

    /*
    Entity(DB와 1:1 매칭되는 객체)인데 DB의 컬럼이 아닌 필드를 선언해도 되는가?
    - JPA(구현된 인터페이스로 쿼리를 처리)를 사용한다면 위배되는 코드
    - Mybatis에서는 Entity가 Dto의 개념을 함께 가지고 있기 때문에 컬럼 이외의 필드 선언 가능
    - SELECT 쿼리문에서 가져올 컬럼의 개수보다 필드의 개수가 많아서 발생하는
      IndexOutOfBoundsException 예외를 방지하려면
      @NoArgsConstructor 생성자 선언 필수
    DB JOIN으로 데이터를 가져오는 것이 아닌 필드로 추가하는 이유는?
    - JAVA 객체 지향 설계를 위해 객체를 참조할 수 있는 구조로 구현
     */
    private List<UserRole> userRoles;

}


