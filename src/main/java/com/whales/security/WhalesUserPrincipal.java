package com.whales.security;

import com.whales.user.domain.User;
import com.whales.user.domain.UserRole;
import com.whales.user.domain.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class WhalesUserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final UserRole role;
    private final UserStatus status;

    public WhalesUserPrincipal(UUID id, String email, UserRole role, UserStatus status) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public static WhalesUserPrincipal from(User user) {
        return new WhalesUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }

    public UUID getId() {
        return id;
    }

    public UserRole getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    public String getPassword() {
        return ""; // JWT 기반 — 패스워드 미사용
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 차단 유저면 false 반환 → Security에서 로그인 차단됨
        return status != UserStatus.BANNED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // ACTIVE만 OK — SOFT_BLOCK 등 중간 단계도 고려 가능
        return status == UserStatus.ACTIVE;
    }
}