package common.utils;

import common.HumanBeingChecker;

import java.util.Scanner;

public class BooleanBuilder {
    private final Scanner scanner;

    public BooleanBuilder(Scanner scanner) {
        this.scanner = scanner;
    }
    public Boolean readBoolean(String message) {
        while (true) {
            System.out.print(message + " (true/false): ");
            String input = this.scanner.nextLine().trim().toLowerCase();

            if (!input.equalsIgnoreCase("true") && !input.equalsIgnoreCase("false")) {
                System.out.println("Ошибка: введите 'true' или 'false'!");
                continue;
            }

            try {
                return Boolean.parseBoolean(input.toLowerCase());
            } catch (Exception E) {
                System.out.printf("Ошибка: %s\n", E.getMessage());
            }
        }
    }
}
