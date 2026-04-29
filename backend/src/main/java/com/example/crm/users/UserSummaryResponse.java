package com.example.crm.users;

import java.util.UUID;

public record UserSummaryResponse(UUID id, String fullName, String email, Role role) {
    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
