package com.example.crm.invitations;

import static org.assertj.core.api.Assertions.*;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class InvitationLimitsTest {
    @Test void limitsRepeatedRequestsWithoutBlockingOtherKeys() {
        var limits = new InvitationLimits();
        limits.check("client-a", 1, Duration.ofMinutes(1));
        assertThatThrownBy(() -> limits.check("client-a", 1, Duration.ofMinutes(1))).isInstanceOf(InvitationException.class);
        assertThatCode(() -> limits.check("client-b", 1, Duration.ofMinutes(1))).doesNotThrowAnyException();
    }
    @Test void tokensHaveRequiredEntropyAndSafeRepresentation() {
        String one = InvitationTokens.generate(), two = InvitationTokens.generate();
        assertThat(one.matches("[A-Za-z0-9_-]{43}")).isTrue();
        assertThat(one.equals(two)).isFalse();
        assertThat(InvitationTokens.hash(one).length()).isEqualTo(64);
    }
    @Test void emailEscapesUntrustedEmployeeNames() {
        String html = InvitationEmail.html("<script>unsafe</script>", "Associate", "https://example.test/activate", java.time.Instant.now());
        assertThat(html.contains("<script>")).isFalse();
        assertThat(html).contains("&lt;script&gt;");
    }
}
