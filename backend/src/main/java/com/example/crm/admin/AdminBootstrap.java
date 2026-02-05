package com.example.crm.admin;

import com.example.crm.users.Role;
import com.example.crm.users.User;
import com.example.crm.users.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class AdminBootstrap {
    @Bean CommandLineRunner seedAdmin(UserRepository users, PasswordEncoder passwordEncoder,
        @Value("${crm.bootstrap.admin.enabled:false}") boolean enabled,
        @Value("${crm.bootstrap.admin.email:admin@example.com}") String email,
        @Value("${crm.bootstrap.admin.password:change-me}") String password) {
        return args -> {
            if (enabled && !users.existsByRole(Role.ADMIN)) {
                users.save(new User("Initial Admin", email, passwordEncoder.encode(password), Role.ADMIN));
            }
        };
    }
}
