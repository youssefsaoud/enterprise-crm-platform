package com.example.crm.admin;

import com.example.crm.auth.AuthService;
import com.example.crm.auth.dto.CreateUserRequest;
import com.example.crm.auth.dto.CurrentUserResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {
    private final AuthService authService;
    public AdminUserController(AuthService authService) { this.authService = authService; }
    @PostMapping public CurrentUserResponse createUser(@Valid @RequestBody CreateUserRequest request) { return authService.createUser(request); }
}
