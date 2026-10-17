package com.example.crm.auth;

import com.example.crm.auth.dto.AuthResponse;
import com.example.crm.auth.dto.CurrentUserResponse;
import com.example.crm.auth.dto.LoginRequest;
import com.example.crm.security.CrmUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Sign in and obtain a JWT", description = "Public login for an active account. The returned accessToken authorizes subsequent requests; never share it.")
    @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest request) { return authService.login(request); }
    @GetMapping("/me") public CurrentUserResponse me(@AuthenticationPrincipal CrmUserPrincipal principal) { return authService.currentUser(principal); }
}
