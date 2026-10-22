package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // /testdb는 permitAll 목록에서 제외
                .requestMatchers("/", "/hello", "/hello2", "/detailed_web.html",
                                 "/login", "/signup", "/error").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()
                // 요구사항 2: /testdb는 ADMIN, MANAGER만 허용 (USER 접근 시 403)
                .requestMatchers("/testdb").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/hello").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
            )
            .rememberMe(remember -> remember
                .key("비밀키")
                .tokenValiditySeconds(60 * 60 * 24 * 7)
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                request -> request.getRequestURI().startsWith("/api/")
            ));

        return http.build();
    }
}