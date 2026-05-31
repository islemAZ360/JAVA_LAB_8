package server.auth;

public class UserSession {
    private final String username;
    private final long loginTime;

    public UserSession(String username) {
        this.username = username;
        this.loginTime = System.currentTimeMillis();
    }

    public String getUsername() { return username; }
    public long getLoginTime() { return loginTime; }
}
