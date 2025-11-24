package com.whales.security;

import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import com.whales.user.domain.UserStatus;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Authorization 헤더 없음 → 인증 없이 진행 (public API용)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateAndParse(token);
            UUID userId = jwtUtil.extractUserId(claims);

            // DB의 실제 유저 가져오기 (BAN 된 유저는 차단)
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            if (user.getStatus() == UserStatus.BANNED) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("""
                {
                  "error": "ACCOUNT_BANNED",
                  "message": "This account is banned."
                }
                """);
                return;
            }

            // Principal 생성
            WhalesUserPrincipal principal = WhalesUserPrincipal.from(user);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ex) {
            // Access Token invalid → 프론트 refresh 흐름 발동
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
            {
              "error": "TOKEN_EXPIRED",
              "message": "Invalid or expired access token"
            }
            """);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
