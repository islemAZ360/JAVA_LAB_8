package common.utils;

import java.util.Scanner;

public class LongBuilder {
    private Scanner scanner;

    public LongBuilder(Scanner scanner) {
        this.scanner = scanner;
    }

    public Long readLongId() {
        while (true) {
            System.out.print("Введите ID (long): ");
            String input = this.scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Ошибка: ID не может пустым!");
                continue;
            }

            try {
                long id = Long.parseLong(input);

                if (id <= 0) {
                    System.out.println("Ошибка: ID должен быть больше 0!");
                    continue;
                }

                return id;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Неверный формат числа. Введите целое число (long)!");
            }
        }
    }
}
