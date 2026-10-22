package com.example.demo.model.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.model.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 아이디로 회원 단일 조회
    Optional<Member> findByUsername(String username);

    // 아이디 중복 확인
    boolean existsByUsername(String username);

    // 요구사항: 아이디나 이름에 검색어가 들어간 회원 검색
    List<Member> findByUsernameContainingOrNameContaining(String usernameKeyword, String nameKeyword);
}