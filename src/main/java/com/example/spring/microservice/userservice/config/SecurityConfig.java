package com.example.spring.microservice.userservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ── Disable CSRF — stateless REST API uses JWT not sessions
                .csrf(AbstractHttpConfigurer::disable)

                // ── Stateless — no HTTP session ever created or used
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ── Route-level access rules ─────────────────────
                .authorizeHttpRequests(auth -> auth

                        // Internal endpoints — only accessible from within
                        // the private network (enforced at infrastructure level)
                        // but we still restrict by role here as a second layer
                        .requestMatchers("/internal/**").hasAnyRole("USER", "ADMIN")

                        // User's own profile — any authenticated user
                        .requestMatchers("/users/me").hasAnyRole("USER", "ADMIN")

                        // Admin-only endpoints
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        // Reject everything else
                        .anyRequest().authenticated()
                )

                // ── Register JWT filter before Spring's default auth filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}