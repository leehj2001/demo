package com.example.demo.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.domain.Member;
import com.example.demo.model.dto.MemberForm;
import com.example.demo.model.dto.MemberResponse;
import com.example.demo.model.dto.RoleRequest;
import com.example.demo.model.service.MemberService;

@RestController
@RequestMapping("/api/admin/members")
public class AdminApiController {

    @Autowired
    private MemberService memberService;

    // 요구사항 1: 회원 검색 API (GET /api/admin/members?keyword=...)
    @GetMapping
    public List<MemberResponse> list(@RequestParam(required = false) String keyword) {
        List<Member> members = memberService.findAll(keyword);
        return members.stream().map(MemberResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> get(@PathVariable Long id) {
        return memberService.findById(id)
                .map(member -> ResponseEntity.ok(MemberResponse.from(member)))
                .orElse(ResponseEntity.notFound().build());
    }

    // 요구사항 2: 반복되는 try-catch 제거 (@RestControllerAdvice에서 일괄 처리)
    @PostMapping
    public ResponseEntity<MemberResponse> create(@RequestBody MemberForm form) {
        Member saved = memberService.signup(form);
        return ResponseEntity
                .created(URI.create("/api/admin/members/" + saved.getId()))
                .body(MemberResponse.from(saved));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<MemberResponse> changeRole(@PathVariable Long id, @RequestBody RoleRequest request) {
        Member member = memberService.changeRole(id, request.role());
        return ResponseEntity.ok(MemberResponse.from(member));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}