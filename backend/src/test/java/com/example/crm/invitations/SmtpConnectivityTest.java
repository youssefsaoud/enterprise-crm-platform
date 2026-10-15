package com.example.crm.invitations;

import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootTest
@EnabledIfSystemProperty(named = "crm.smtp.smoke", matches = "true")
class SmtpConnectivityTest {
    @Autowired JavaMailSenderImpl sender;
    @Test void authenticatesOverRequiredStartTlsWithoutSendingMail() {
        try { sender.testConnection(); }
        catch (Exception ignored) { fail("SMTP connectivity or authentication failed; no credentials or server details were logged."); }
    }
}
