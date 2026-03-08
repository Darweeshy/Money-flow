package com.example.spring.microservice.userservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Security filter that protects /internal/** endpoints.
 * Requests to /internal/** must carry an X-Internal-Secret header matching
 * the configured secret. This prevents external callers from hitting internal
 * endpoints — only trusted services (e.g. AuthService) can call them.
 */
@Component
public class InternalAuthFilter extends OncePerRequestFilter {

    private static final String INTERNAL_SECRET_HEADER = "X-Internal-Secret";
    private static final String INTERNAL_PATH_PREFIX = "/internal/";

    @Value("${internal.secret}")
    private String internalSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Only enforce on /internal/** routes
        if (!path.startsWith(INTERNAL_PATH_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedSecret = request.getHeader(INTERNAL_SECRET_HEADER);

        if (providedSecret == null || !providedSecret.equals(internalSecret)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":403,\"error\":\"Forbidden\"," +
                    "\"message\":\"Missing or invalid internal secret\"}"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
