package com.example.clouddisk.security.jwt;

import com.example.clouddisk.models.Role;
import com.example.clouddisk.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public final class JwtUserFactory {

    public JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getMail(),
                user.getPassword(),
                mapToGrantedAuthorities(new ArrayList<>(user.getRoles())),
                true
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<Role> userRoles) {
        return userRoles.stream()
                .map(role ->
                        new SimpleGrantedAuthority(role.getRole())
                ).collect(Collectors.toList());
    }
}
