package com.example.cinemakiosk.service.AdminService;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class AdminDetails extends User {
    private final boolean level;

    public AdminDetails(String username, String password, boolean level, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.level = level;
    }
}
