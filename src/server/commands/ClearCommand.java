package server.commands;

import common.*;
import server.CollectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Команда clear: очищает коллекцию (удаляет все элементы).
 * Version có xác nhận từ người dùng trước khi xóa.
 */
public class ClearCommand implements Command {

    private final CollectionManager collectionManager;
    private final Scanner scanner;

    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "clear : очистить коллекцию (с подтверждением)";
    }

//    @Override
//    public String execute(String[] args) {
//        System.out.println("В коллекции " + collectionManager.size() + " элементов.");
//        System.out.print("Вы уверены, что хотите очистить коллекцию? (yes/no): ");
//
//        String answer = scanner.nextLine().trim().toLowerCase();
//
//        if (answer.equals("yes") || answer.equals("y")) {
//            int oldSize = collectionManager.size();
//            collectionManager.clear();
//            return "Коллекция очищена. Удалено элементов: " + oldSize + "\nНе забудьте ввести 'save' для сохранения изменений в файл.";
//
//        } else {
//            return "Операция отменена.";
//        }
//    }

    @Override
    public Response execute(Request request) {

        if (collectionManager.isEmpty()) {
            return new Response("Коллекция уже пуста", StatusCode.OK, null);
        }

        String arg = request.getStringArgument();

        if (arg != null) {
            String flag = arg.trim().toLowerCase();

            if (flag.equals(CommandFlag.YES.getLongFlag()) ||
                flag.equals(CommandFlag.YES.getShortFlag()) ||
                flag.equals(CommandFlag.FORCE.getLongFlag()) ||
                flag.equals(CommandFlag.FORCE.getShortFlag())) {
                int size = collectionManager.size();
                collectionManager.clear();

                return new Response(
                        "Коллекция очищена. Удалено элементов: " + size,
                        StatusCode.OK,
                        null
                );
            }

            if (flag.equals("no") || flag.equals("-n")) {
                return new Response("Операция отменена.", StatusCode.OK, null);
            }

            return new Response(String.format("Неверный аргумент. Используйте флаги %s или %s", CommandFlag.FORCE.getLongFlag(), CommandFlag.YES.getLongFlag()), StatusCode.BAD_REQUEST, null);
        }

        return new Response(
                "В коллекции " + collectionManager.size() + " элементов.\n" +
                        "Вы уверены? (yes/no)",
                StatusCode.CONTINUE,
                null
        );
    }
}
