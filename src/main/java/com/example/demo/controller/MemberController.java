package com.example.demo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.domain.Member;
import com.example.demo.model.dto.MemberForm;
import com.example.demo.model.service.MemberService;

@Controller
public class MemberController {

    @Autowired 
    MemberService memberService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(MemberForm memberForm, Model model) {
        // 요구사항: password와 passwordConfirm 비교 검증
        if (memberForm.getPassword() == null || !memberForm.getPassword().equals(memberForm.getPasswordConfirm())) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("memberForm", memberForm); // 입력값 유지
            return "signup"; // DB 저장(memberService.signup)을 호출하지 않고 가입 폼 반환
        }

        try {
            memberService.signup(memberForm);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("memberForm", memberForm);
            return "signup";
        }

        return "redirect:/login?signup";
    }

    @GetMapping("/mypage")
    public String mypage(Principal principal, Model model) {
        Member member = memberService.findByUsername(principal.getName());
        model.addAttribute("member", member);
        return "mypage";
    }
}