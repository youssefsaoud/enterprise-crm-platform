package com.example.crm.auth.dto;

import com.example.crm.users.Role;
import java.util.UUID;

public record CurrentUserResponse(UUID id, String fullName, String email, Role role) {
}
