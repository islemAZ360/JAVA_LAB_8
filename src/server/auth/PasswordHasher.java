package server.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordHasher {
    private PasswordHasher() {
    }

    public static String hashSha384(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password can not be null");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-384");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            return toHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-384 algorithm is not available", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);

        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }
}