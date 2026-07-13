package com.example.GymManagementSystem.security;

import com.example.GymManagementSystem.model.Admin;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AdminPrincipal implements UserDetails {
    private final Admin admin;

    public AdminPrincipal(Admin admin) { this.admin = admin; }
    public Long getId() { return admin.getId(); }
    public String getRole() { return admin.getRole(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + admin.getRole()));
    }

    @Override public String getPassword() { return admin.getPasswordHash(); }
    @Override public String getUsername() { return admin.getUsername(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return admin.getLockTime() == null; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return admin.isEnabled(); }
}
