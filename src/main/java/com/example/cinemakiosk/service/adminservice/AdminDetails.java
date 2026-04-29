package com.example.cinemakiosk.service.adminservice;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class AdminDetails extends User {
    private final boolean level;

    // 일반 User로 받아오려니 권한(level)을 가져올 수 없어서 따로 만듬
    public AdminDetails(String username, String password, boolean level, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.level = level;
    }
}
