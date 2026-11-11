package com.example.demo.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.example.demo.model.domain.Member;
import com.example.demo.model.dto.MemberForm;
import com.example.demo.model.repository.MemberRepository;
import com.example.demo.model.repository.PostRepository;
import com.example.demo.model.repository.CommentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class MemberService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired // ① 필드 추가
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;


    private static final List<String> ROLES = List.of("USER", "MANAGER", "ADMIN");

    private boolean isBlank(String value) { 
        return value == null || value.isBlank(); 
    }

    public Member signup(MemberForm form) {
        if (isBlank(form.getUsername()) || isBlank(form.getPassword()) || isBlank(form.getName())) {
            throw new IllegalArgumentException("아이디, 비밀번호, 이름은 필수입니다.");
        }

        if (form.getPasswordConfirm() == null || !form.getPassword().equals(form.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (memberRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        Member member = new Member();
        member.setUsername(form.getUsername());
        member.setPassword(passwordEncoder.encode(form.getPassword()));
        member.setName(form.getName());
        member.setRole("USER");
        return memberRepository.save(member);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("회원 없음 : " + username));
        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(member.getRole())
                .build();
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다 : " + username));
    }

    public List<Member> findAll() {
        return memberRepository.findAll(Sort.by("id"));
    }

    // 요구사항: 키워드 검색 지원 (아이디 또는 이름에 포함된 회원 조회)
    public List<Member> findAll(String keyword) {
        if (isBlank(keyword)) {
            return findAll();
        }
        return memberRepository.findByUsernameContainingOrNameContaining(keyword, keyword);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Member changeRole(Long id, String role) {
        if (!ROLES.contains(role)) {
            throw new IllegalArgumentException("허용되지 않는 권한입니다 : " + role);
        }
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        member.setRole(role);
        return memberRepository.save(member);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMember(Long id) { // 회원 삭제
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        checkNotMe(member); // [6주차 연습문제 1]
        if (postRepository.existsByAuthor(member) || commentRepository.existsByAuthor(member)) {
            throw new IllegalArgumentException("작성한 글이나 댓글이 있는 회원은 삭제할 수 없습니다.");
        }
        memberRepository.delete(member); // DELETE
    }

    private void checkNotMe(Member member) { // [6주차 연습문제 1] 로그인한 본인은 삭제 불가
        String loginUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (member.getUsername().equals(loginUsername)) {
            throw new IllegalArgumentException("자기 자신은 삭제할 수 없습니다.");
        }
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }
}