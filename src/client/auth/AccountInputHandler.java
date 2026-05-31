package client.auth;

import java.util.Scanner;
import java.util.regex.Pattern;

public class AccountInputHandler {
    private final Scanner scanner;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    public AccountInputHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    public Account getRegisterInfo() {
        System.out.println("\n===== РЕГИСТРАЦИЯ АККАУНТА =====");

        String username = "";
        while (true) {
            System.out.print("Введите имя пользователя: ");
            username = scanner.nextLine().trim();
            if (validateUsername(username)) {
                break;
            }
        }

        String password = "";
        while (true) {
            System.out.print("Введите пароль: ");
            password = scanner.nextLine();
            if (validatePassword(password)) {
                break;
            }
        }

        String email = null;
        while (true) {
            System.out.print("Введите email (или оставьте пустым): ");
            String inputEmail = scanner.nextLine().trim();

            if (inputEmail.isEmpty()) {
                email = null;
                break;
            }

            if (validateEmail(inputEmail)) {
                email = inputEmail;
                break;
            }
        }

        return new Account(username, password, email);
    }

    public Account getLoginInfo() {
        System.out.println("\n===== ВХОД В СИСТЕМУ =====");

        String username = "";
        while (true) {
            System.out.print("Имя пользователя: ");
            username = scanner.nextLine().trim();
            if (!username.isEmpty()) {
                break;
            }
            System.out.println("❌ Ошибка: Имя пользователя не может быть пустым!");
        }

        String password = "";
        while (true) {
            System.out.print("Пароль: ");
            password = scanner.nextLine();
            if (!password.isEmpty()) {
                break;
            }
            System.out.println("❌ Ошибка: Пароль не может быть пустым!");
        }

        return new Account(username, password);
    }

    private boolean validateUsername(String username) {
        if (username.length() < 8) {
            System.out.println("❌ Ошибка: Имя пользователя должно быть не менее 8 символов!");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.length() < 8) {
            System.out.println("❌ Ошибка: Пароль должен быть не менее 8 символов!");
            return false;
        }
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            System.out.println("❌ Ошибка: Пароль должен содержать хотя бы одну заглавную букву!");
            return false;
        }
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            System.out.println("❌ Ошибка: Пароль должен содержать хотя бы одну строчную букву!");
            return false;
        }
        if (!DIGIT_PATTERN.matcher(password).find()) {
            System.out.println("❌ Ошибка: Пароль должен содержать хотя бы одну цифру!");
            return false;
        }
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            System.out.println("❌ Ошибка: Пароль должен содержать хотя бы один специальный символ!");
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            System.out.println("❌ Ошибка: Неверный формат email!");
            return false;
        }
        return true;
    }
}
