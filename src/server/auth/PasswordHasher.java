package server.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordHasher {
    private PasswordHasher() {
    }

    public enum HashAlgorithm {
        SHA256("SHA-256"),
        SHA384("SHA-384");

        private final String name;

        HashAlgorithm(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static String hashSha384(String password) {
        return hashWithAlgorithm(password, HashAlgorithm.SHA384);
    }

    public static String hashSha256(String password) {
        return hashWithAlgorithm(password, HashAlgorithm.SHA256);
    }

    private static String hashWithAlgorithm(String password, HashAlgorithm algorithm) {
        if (password == null) {
            throw new IllegalArgumentException("Password can not be null");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm.getName());
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return toHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(algorithm.getName() + " algorithm is not available", e);
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
