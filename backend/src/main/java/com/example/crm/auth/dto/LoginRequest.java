package com.example.crm.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(@Email @NotBlank String email,
    @Schema(format = "password", accessMode = Schema.AccessMode.WRITE_ONLY) @NotBlank String password) {
}
