package com.example.class_registration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/student/login",
                    "/student/register",
                    "/instructor/login",
                    "/instructor/register",
                    "/WEB-INF/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/error",
                    "/error/**"
                ).permitAll()
                .requestMatchers(
                    "/student/home",
                    "/student/drop",
                    "/student/logout"
                ).hasRole("STUDENT")
                .requestMatchers(
                    "/instructor/home",
                    "/instructor/course/**",
                    "/instructor/logout",
                    "/instructor/add-course"
                ).hasRole("INSTRUCTOR")
                .requestMatchers(
                    "/api/courses/display",
                    "/api/courses/*/info",
                    "/api/courses",
                    "/api/courses/available"
                ).hasAnyRole("STUDENT", "INSTRUCTOR")
                .requestMatchers(
                    "/api/courses/*/register",
                    "/api/registrations/**"
                ).hasRole("STUDENT")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    String uri = request.getRequestURI();
                    if (uri.startsWith("/instructor")) {
                        response.sendRedirect("/instructor/login");
                    } else {
                        response.sendRedirect("/student/login");
                    }
                })
                .accessDeniedHandler((request, response, ex2) ->
                    response.sendRedirect("/error/403")
                )
            )
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }
}