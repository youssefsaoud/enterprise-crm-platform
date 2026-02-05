package com.example.crm.auth;

import com.example.crm.auth.dto.AuthResponse;
import com.example.crm.auth.dto.CurrentUserResponse;
import com.example.crm.auth.dto.LoginRequest;
import com.example.crm.security.CrmUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }
    @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest request) { return authService.login(request); }
    @GetMapping("/me") public CurrentUserResponse me(@AuthenticationPrincipal CrmUserPrincipal principal) { return authService.currentUser(principal); }
}
