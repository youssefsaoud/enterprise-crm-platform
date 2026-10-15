package com.example.crm.invitations;

import com.example.crm.users.Role;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public final class InvitationDtos {
    private InvitationDtos() {}

    public record Create(@NotBlank @Size(max = 120) String fullName,
                         @NotBlank @Email @Size(max = 180) String email, @NotNull Role role) {
        @AssertTrue(message = "Only Manager or Associate invitations are permitted")
        public boolean isEmployeeRole() { return role == Role.MANAGER || role == Role.SALES_REP; }
    }

    public record Token(@NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{43}") String token) {
        @Override public String toString() { return "Token[redacted]"; }
    }
    public record Accept(@NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{43}") String token,
                         @NotBlank @Size(min = 12, max = 64)
                         @Pattern(regexp = "(?s)(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).*",
                             message = "Use uppercase, lowercase, and a number") String password) {
        @Override public String toString() { return "Accept[redacted]"; }
    }
    public record InvitationView(UUID id, String fullName, String email, Role role, String status,
                                 String deliveryStatus, Instant createdAt, Instant expiresAt,
                                 Instant lastSentAt, Instant acceptedAt, int deliveryAttempts) {
        static InvitationView from(EmployeeInvitation i) {
            return new InvitationView(i.id, i.fullName, i.email, i.role, i.displayStatus(Instant.now()),
                i.deliveryStatus, i.createdAt, i.expiresAt, i.lastSentAt, i.acceptedAt, i.deliveryAttempts);
        }
    }
    public record EmployeeView(UUID id, String fullName, String email, Role role, boolean active, Instant createdAt) {}
    public record Welcome(String fullName, Role role, Instant expiresAt) {}
    public record Delivery(InvitationView invitation, boolean submitted, String message) {}
}
