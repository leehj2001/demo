package com.example.demo.model.service;

import java.util.List; // List 컬렉션 임포트 추가

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.domain.TestDB;
import com.example.demo.model.repository.TestRepository;

@Service // 서비스 등록, 자동 등록됨
public class TestService {

    @Autowired // 객체 의존성 주입 DI(컨테이너 내부 등록)
    private TestRepository testRepository;

    // 기존 단일 사용자 검색 메서드
    public TestDB findByName(String name) {
        return (TestDB) testRepository.findByName(name);
    }

    // 요구사항: 전체 사용자 목록 조회를 위한 메서드 추가
    public List<TestDB> findAll() {
        return testRepository.findAll();
    }
}