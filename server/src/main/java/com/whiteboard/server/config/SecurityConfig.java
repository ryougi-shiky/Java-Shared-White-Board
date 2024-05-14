package com.whiteboard.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF 保护，适用于API服务器
                .csrf(csrf -> csrf.disable())
                // 配置请求授权
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()  // 允许无授权访问"/api/register"
                        .anyRequest().authenticated()  // 其他所有请求需要认证
                )
                // 配置 HTTP Basic 认证，适用于简单认证场景
                .httpBasic(httpBasic -> {});

        return http.build();
    }
}
