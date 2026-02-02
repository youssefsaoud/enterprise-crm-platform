package com.example.crm.auth.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresIn) {
}
