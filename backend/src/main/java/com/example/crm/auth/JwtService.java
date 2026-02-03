package com.example.crm.auth;

import com.example.crm.security.CrmUserPrincipal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final byte[] secret;
    private final long expiresIn;
    private final String issuer;

    public JwtService(@Value("${crm.jwt.secret}") String secret, @Value("${crm.jwt.expires-in-seconds:3600}") long expiresIn, @Value("${crm.jwt.issuer:crm-api}") String issuer) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expiresIn = expiresIn;
        this.issuer = issuer;
    }

    public String createToken(CrmUserPrincipal user) {
        long now = Instant.now().getEpochSecond();
        String header = jsonBase64("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = jsonBase64("{\"iss\":\"" + issuer + "\",\"sub\":\"" + user.email() + "\",\"role\":\"" + user.role().name() + "\",\"iat\":" + now + ",\"exp\":" + (now + expiresIn) + "}");
        return header + "." + payload + "." + sign(header + "." + payload);
    }

    public String subject(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3 || !sign(parts[0] + "." + parts[1]).equals(parts[2])) throw new IllegalArgumentException("Invalid token");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        long exp = Long.parseLong(extract(payload, "exp"));
        if (Instant.now().getEpochSecond() >= exp) throw new IllegalArgumentException("Expired token");
        String tokenIssuer = extractString(payload, "iss");
        if (!issuer.equals(tokenIssuer)) throw new IllegalArgumentException("Invalid issuer");
        return extractString(payload, "sub");
    }

    public long expiresIn() { return expiresIn; }

    private String jsonBase64(String json) { return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8)); }
    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) { throw new IllegalStateException("Unable to sign token", ex); }
    }
    private String extractString(String json, String key) { return extract(json, key).replace("\"", ""); }
    private String extract(String json, String key) {
        String marker = "\"" + key + "\":";
        int start = json.indexOf(marker);
        if (start < 0) throw new IllegalArgumentException("Missing claim");
        int valueStart = start + marker.length();
        int end = json.indexOf(',', valueStart);
        if (end < 0) end = json.indexOf('}', valueStart);
        return json.substring(valueStart, end);
    }
}
