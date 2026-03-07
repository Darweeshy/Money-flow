package com.example.spring.microservice.userservice.config;
import com.example.spring.microservice.userservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // ── 1. Extract Authorization header ─────────────────
        String authHeader = request.getHeader("Authorization");
        String token = jwtUtil.stripBearer(authHeader);

        // ── 2. If no token, continue — SecurityConfig will
        //       reject if the endpoint requires authentication
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // ── 3. Validate token ────────────────────────────────
        if (!jwtUtil.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ── 4. Extract claims ────────────────────────────────
        String userId = jwtUtil.extractUserId(token).toString();
        String role   = jwtUtil.extractRole(token);

        // ── 5. Build authentication and set in context ───────
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // ── 6. Continue filter chain ─────────────────────────
        filterChain.doFilter(request, response);
    }
}
