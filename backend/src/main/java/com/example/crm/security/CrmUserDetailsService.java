package com.example.crm.security;

import com.example.crm.users.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CrmUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public CrmUserDetailsService(UserRepository users) { this.users = users; }
    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.findByNormalizedEmail(username.trim().toLowerCase()).map(CrmUserPrincipal::new).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
