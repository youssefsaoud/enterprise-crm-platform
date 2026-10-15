package com.example.crm.invitations;

import java.time.Instant;
import java.util.UUID;

public interface InvitationMail {
    /** Returns SENT for SMTP handoff or PREVIEW for explicitly enabled local preview. */
    String send(UUID invitationId, String email, String fullName, String role, String activationUrl, Instant expiresAt);
}
