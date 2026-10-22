package com.example.demo.model.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "testdb")
@Data
public class TestDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 요구사항: 나이, 성별 컬럼 추가
    private Integer age;

    private String gender;
}