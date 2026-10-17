package com.example.crm.security;

import com.example.crm.users.UserRepository;
import com.example.crm.demo.RecruiterDemoGuard;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CrmUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    private final Environment environment;
    public CrmUserDetailsService(UserRepository users, Environment environment) { this.users = users; this.environment = environment; }
    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.trim().toLowerCase(java.util.Locale.ROOT).endsWith("@orqestra.demo") && !RecruiterDemoGuard.enabled(environment)) {
            throw new UsernameNotFoundException("Local demo accounts are disabled");
        }
        return users.findByNormalizedEmail(username.trim().toLowerCase()).map(CrmUserPrincipal::new).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
