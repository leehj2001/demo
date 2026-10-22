package com.example.demo.controller;

import com.example.demo.model.service.TestService;
import com.example.demo.model.domain.TestDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    @Autowired 
    TestService testService;

    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("data", "방갑습니다.");
        return "hello";
    }

    // 요구사항: hello2 메서드 작성 및 5개 속성 추가
    @GetMapping("/hello2")
    public String hello2(Model model) {
        model.addAttribute("data1", "홍길동님.");
        model.addAttribute("data2", "방갑습니다.");
        model.addAttribute("data3", "오늘.");
        model.addAttribute("data4", "날씨는.");
        model.addAttribute("data5", "매우 좋습니다.");
        return "hello2";
    }

    @GetMapping("/testdb")
    public String getAllTestDBs(Model model) {
        TestDB test = testService.findByName("홍길동");
        model.addAttribute("data4", test);
        System.out.println("데이터 출력 디버그 : " + test);
        return "testdb";
    }

    @GetMapping("/detailed")
    public String detailed() {
        return "detailed"; // templates/detailed.html 반환
    }

    @GetMapping("/detailed2")
    public String detailed2() {
        return "detailed2"; // templates/detailed2.html 반환
    }

    @GetMapping("/detailed3")
    public String detailed3() {
        return "detailed3"; // templates/detailed3.html 반환
    }

    @GetMapping("/detailed4")
    public String detailed4() {
        return "detailed4"; // templates/detailed4.html 반환
    }
}