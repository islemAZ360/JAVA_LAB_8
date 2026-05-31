package server.auth;

public enum UserStatus {
    ONLINE("Пользователь в сети"),
    OFFLINE("Пользователь вышел из системы"),
    AWAY("Пользователь отошел"),
    BANNED("Аккаунт заблокирован за нарушение правил");

    private final String message;

    UserStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
