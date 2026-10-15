package com.example.crm.invitations;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.HexFormat;

final class InvitationTokens {
    private static final SecureRandom RANDOM = new SecureRandom();
    private InvitationTokens() {}
    static String generate() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    static String hash(String token) {
        if (token == null || !token.matches("[A-Za-z0-9_-]{43}")) throw InvitationException.invalid();
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) { throw new IllegalStateException("SHA-256 unavailable"); }
    }
}
