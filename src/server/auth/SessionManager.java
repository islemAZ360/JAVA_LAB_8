package server.auth;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, String> activeSessions = new ConcurrentHashMap<>();

    public static String createSession(String username) {
        String token = UUID.randomUUID().toString();
        activeSessions.put(token, username);
        return token;
    }

    public static String getUsername(String token) {
        return activeSessions.get(token);
    }

    public static void removeSession(String token) {
        activeSessions.remove(token);
    }

    public static boolean isLoggedIn(String token) {
        return activeSessions.containsKey(token);
    }
}
