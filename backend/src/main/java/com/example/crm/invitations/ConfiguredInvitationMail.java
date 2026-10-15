package com.example.crm.invitations;

import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

@Service
public class ConfiguredInvitationMail implements InvitationMail {
    private final JavaMailSender sender;
    private final String username;
    private final String mode;
    private final boolean previewAllowed;
    private final InvitationPreview preview;

    public ConfiguredInvitationMail(JavaMailSender sender, Environment environment, InvitationPreview preview,
        @Value("${spring.mail.username:}") String username,
        @Value("${crm.invitations.delivery-mode:smtp}") String mode,
        @Value("${crm.invitations.preview-enabled:false}") boolean previewEnabled) {
        this.sender = sender; this.username = username; this.mode = mode; this.preview = preview;
        this.previewAllowed = previewEnabled && environment.acceptsProfiles(Profiles.of("dev"));
    }

    public String send(UUID id, String email, String name, String role, String url, Instant expiry) {
        if ("preview".equals(mode)) {
            if (!previewAllowed) throw new IllegalStateException("Preview is disabled");
            preview.put(id, InvitationEmail.html(name, role, url, expiry), expiry);
            return "PREVIEW";
        }
        if (!"smtp".equals(mode) || username.isBlank()) throw new IllegalStateException("SMTP is not configured");
        try {
            var message = sender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(username, "Orqestra"); helper.setTo(email);
            helper.setSubject("You're invited to Orqestra");
            helper.setText(InvitationEmail.text(name, role, url, expiry), InvitationEmail.html(name, role, url, expiry));
            sender.send(message);
            return "SENT";
        } catch (Exception ignored) {
            // Mail exceptions can contain recipient/configuration details. Never log or return them.
            throw new IllegalStateException("Invitation email could not be submitted");
        }
    }
}
