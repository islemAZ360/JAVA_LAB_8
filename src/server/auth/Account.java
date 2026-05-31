package server.auth;

public class Account {
    private String username;
    private String hashedPassword;
    private String email;
    private UserStatus status;

    public Account(String username, String hashedPassword, String email) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.email = email;
    }

    public Account(String username, String hashedPassword) {
        this(username, hashedPassword, null);
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
