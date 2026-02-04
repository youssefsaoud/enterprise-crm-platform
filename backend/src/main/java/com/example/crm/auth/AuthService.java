package com.example.crm.auth;

import com.example.crm.auth.dto.AuthResponse;
import com.example.crm.auth.dto.CreateUserRequest;
import com.example.crm.auth.dto.CurrentUserResponse;
import com.example.crm.auth.dto.LoginRequest;
import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.User;
import com.example.crm.users.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository users, PasswordEncoder passwordEncoder) { this.authenticationManager = authenticationManager; this.jwtService = jwtService; this.users = users; this.passwordEncoder = passwordEncoder; }
    public AuthResponse login(LoginRequest request) {
        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password()));
        String token = jwtService.createToken((CrmUserPrincipal) auth.getPrincipal());
        return new AuthResponse(token, "Bearer", jwtService.expiresIn());
    }
    public CurrentUserResponse currentUser(CrmUserPrincipal principal) { return new CurrentUserResponse(principal.id(), principal.fullName(), principal.email(), principal.role()); }
    @Transactional public CurrentUserResponse createUser(CreateUserRequest request) {
        User user = new User(request.fullName(), request.email(), passwordEncoder.encode(request.password()), request.role());
        User saved = users.save(user);
        return new CurrentUserResponse(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRole());
    }
}
