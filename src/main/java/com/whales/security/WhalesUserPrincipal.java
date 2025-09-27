package com.whales.security;

import com.whales.domain.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class WhalesUserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String role;
    private final boolean active;

    public WhalesUserPrincipal(UUID id, String email, String role, boolean active) {
        this.id = id;
        this.email = email;
        this.role = role == null ? "user" : role;
        this.active = active;
    }

    // User Entity에서 바로 생성할 수 있는 팩토리 메서드
    public static WhalesUserPrincipal from(User user,String role) {
        return new WhalesUserPrincipal(
                user.getId(),
                user.getEmail(),
                role,
                user.getStatus().name().equalsIgnoreCase("ACTIVE")
        );
    }

    public UUID getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())
        );
    }

    @Override
    public String getPassword() {
        return ""; // JWT 기반 인증이라 사용하지 않음
    }

    @Override
    public String getUsername() {
        return email; // Security에서 username으로 간주
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
