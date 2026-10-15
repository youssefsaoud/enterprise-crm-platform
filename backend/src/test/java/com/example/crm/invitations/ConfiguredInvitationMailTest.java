package com.example.crm.invitations;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Profiles;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.env.MockEnvironment;

class ConfiguredInvitationMailTest {
    @Test void smtpBuildsBrandedMultipartMailWithoutNetworkCalls() throws Exception {
        var sender = mock(JavaMailSender.class);
        var message = new MimeMessage(Session.getInstance(new Properties()));
        when(sender.createMimeMessage()).thenReturn(message);
        var mail = new ConfiguredInvitationMail(sender, new MockEnvironment(), new InvitationPreview(),
            "sender@example.test", "smtp", false);
        assertThat(mail.send(UUID.randomUUID(), "recipient@example.test", "Taylor", "Associate",
            "https://example.test/activate-account#token=local-test", Instant.now())).isEqualTo("SENT");
        verify(sender).send(message);
        assertThat(message.getSubject()).contains("Orqestra");
        assertThat(message.getFrom()[0].toString()).contains("Orqestra");
        assertThat(message.getContent()).isInstanceOf(jakarta.mail.Multipart.class);
    }

    @Test void previewRequiresBothDevProfileAndExplicitEnablement() {
        var sender = mock(JavaMailSender.class);
        for (boolean enabled : List.of(false, true)) {
            var production = new MockEnvironment(); production.setActiveProfiles("prod");
            var mail = new ConfiguredInvitationMail(sender, production, new InvitationPreview(), "", "preview", enabled);
            assertThatThrownBy(() -> mail.send(UUID.randomUUID(), "test@example.test", "Taylor", "Associate", "https://example.test", Instant.now()))
                .isInstanceOf(IllegalStateException.class);
        }
        verifyNoInteractions(sender);
    }

    @Test void explicitlyEnabledDevPreviewIsEphemeralAndDoesNotSendMail() {
        var sender = mock(JavaMailSender.class);
        var development = new MockEnvironment(); development.setActiveProfiles("dev");
        var preview = new InvitationPreview(); UUID id = UUID.randomUUID();
        var mail = new ConfiguredInvitationMail(sender, development, preview, "", "preview", true);
        assertThat(mail.send(id, "test@example.test", "Taylor", "Associate", "https://example.test", Instant.now().plusSeconds(60)))
            .isEqualTo("PREVIEW");
        assertThat(preview.get(id)).contains("Activate your account");
        verifyNoInteractions(sender);
    }
}
