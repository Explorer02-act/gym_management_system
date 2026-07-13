package com.example.GymManagementSystem.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final ObjectMapper objectMapper;
    private final byte[] secret;
    private final long expirationMs;

    public JwtService(ObjectMapper objectMapper,
                      @Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        this.objectMapper = objectMapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, String role) {
        long now = Instant.now().getEpochSecond();
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", username);
        payload.put("role", role);
        payload.put("iat", now);
        payload.put("exp", now + (expirationMs / 1000));
        String unsigned = base64Url(toJson(header)) + "." + base64Url(toJson(payload));
        return unsigned + "." + sign(unsigned);
    }

    public String extractUsername(String token) { return claims(token).get("sub").toString(); }

    public boolean isValid(String token, String username) {
        Map<String, Object> tokenClaims = claims(token);
        return username.equals(tokenClaims.get("sub")) && !isExpired(tokenClaims) && signatureMatches(token);
    }

    public long getExpirationSeconds() { return expirationMs / 1000; }

    private Map<String, Object> claims(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3 || !signatureMatches(token)) {
            throw new IllegalArgumentException("Invalid token");
        }
        try {
            return objectMapper.readValue(Base64.getUrlDecoder().decode(parts[1]), new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid token", ex);
        }
    }

    private boolean isExpired(Map<String, Object> claims) {
        Object exp = claims.get("exp");
        long expiresAt = exp instanceof Number ? ((Number) exp).longValue() : Long.parseLong(exp.toString());
        return Instant.now().getEpochSecond() >= expiresAt;
    }

    private boolean signatureMatches(String token) {
        String[] parts = token.split("\\.");
        return parts.length == 3 && constantTimeEquals(sign(parts[0] + "." + parts[1]), parts[2]);
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign JWT", ex);
        }
    }

    private byte[] toJson(Map<String, Object> value) {
        try { return objectMapper.writeValueAsBytes(value); }
        catch (Exception ex) { throw new IllegalStateException("Unable to serialize JWT", ex); }
    }

    private String base64Url(byte[] value) { return Base64.getUrlEncoder().withoutPadding().encodeToString(value); }

    private boolean constantTimeEquals(String left, String right) {
        byte[] a = left.getBytes(StandardCharsets.UTF_8);
        byte[] b = right.getBytes(StandardCharsets.UTF_8);
        if (a.length != b.length) { return false; }
        int result = 0;
        for (int i = 0; i < a.length; i++) { result |= a[i] ^ b[i]; }
        return result == 0;
    }
}
