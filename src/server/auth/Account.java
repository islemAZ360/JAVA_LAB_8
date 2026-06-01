package server.auth;

public class Account {
    private Long userId;
    private String username;
    private String hashedPassword;
    private String email;
    private UserStatus status;

    public Account(Long userId, String username, String hashedPassword, String email, UserStatus status) {
        this.userId = userId;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.status = status;
    }

    public Account(String username, String hashedPassword, String email) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.email = email;
    }

    public Account(String username, String hashedPassword) {
        this(username, hashedPassword, null);
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public UserStatus getStatus() {
        return status;
    }
}
