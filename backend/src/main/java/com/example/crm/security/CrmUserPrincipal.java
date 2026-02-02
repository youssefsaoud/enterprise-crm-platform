package com.example.crm.security;

import com.example.crm.users.Role;
import com.example.crm.users.User;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CrmUserPrincipal implements UserDetails {
    private final User user;
    public CrmUserPrincipal(User user) { this.user = user; }
    public UUID id() { return user.getId(); }
    public String fullName() { return user.getFullName(); }
    public String email() { return user.getEmail(); }
    public Role role() { return user.getRole(); }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())); }
    @Override public String getPassword() { return user.getPasswordHash(); }
    @Override public String getUsername() { return user.getNormalizedEmail(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return user.isActive(); }
}
