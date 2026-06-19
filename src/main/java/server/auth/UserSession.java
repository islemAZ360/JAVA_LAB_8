package main.java.server.auth;

public class UserSession {
    private final Long userId;
    private final String username;
    private final long loginTime;

    public UserSession(Long userId, String username) {
        this.userId = userId;
        this.username = username;
        this.loginTime = System.currentTimeMillis();
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public long getLoginTime() { return loginTime; }
}
