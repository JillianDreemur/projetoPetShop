package com.petshop.web.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AdminSessionHelper {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private AdminSessionHelper() {
    }

    public static boolean isAdminLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return auth.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_ADMIN));
    }
}
