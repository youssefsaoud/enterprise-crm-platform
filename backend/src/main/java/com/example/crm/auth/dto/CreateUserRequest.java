package com.example.crm.auth.dto;

import com.example.crm.users.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(@NotBlank String fullName, @Email @NotBlank String email,
    @Schema(format = "password", accessMode = Schema.AccessMode.WRITE_ONLY) @NotBlank String password, @NotNull Role role) {
}
