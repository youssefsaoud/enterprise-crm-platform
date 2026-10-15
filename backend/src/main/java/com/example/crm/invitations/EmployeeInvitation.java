package com.example.crm.invitations;

import com.example.crm.users.Role;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "employee_invitations")
public class EmployeeInvitation {
    @Id UUID id;
    @Column(nullable = false, length = 180) String email;
    @Column(nullable = false, length = 180) String normalizedEmail;
    @Column(nullable = false, length = 120) String fullName;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) Role role;
    @Column(nullable = false, length = 64) String tokenHash;
    @Column(nullable = false, length = 16) String status;
    @Column(nullable = false, length = 16) String deliveryStatus;
    @Column(nullable = false) Instant createdAt;
    @Column(nullable = false) Instant expiresAt;
    Instant acceptedAt;
    Instant revokedAt;
    @Column(nullable = false) Instant lastSentAt;
    @Column(nullable = false) int deliveryAttempts;
    @Column(nullable = false) UUID invitedBy;

    protected EmployeeInvitation() {}

    String displayStatus(Instant now) {
        return "PENDING".equals(status) && !expiresAt.isAfter(now) ? "EXPIRED" : status;
    }
}
