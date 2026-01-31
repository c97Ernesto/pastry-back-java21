package com.malva_pastry_shop.backend.security;

import com.malva_pastry_shop.backend.domain.publicuser.PublicUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record PublicUserPrincipal(
        Long id,
        String email,
        String displayName,
        boolean enabled
) implements UserDetails {

    public static PublicUserPrincipal from(PublicUser publicUser) {
        return new PublicUserPrincipal(
                publicUser.getId(),
                publicUser.getEmail(),
                publicUser.getDisplayName(),
                publicUser.isEnabled());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_PUBLIC_USER"));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
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
}
