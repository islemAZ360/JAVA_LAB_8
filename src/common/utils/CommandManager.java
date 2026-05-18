package common.utils;

import common.Command;

import java.util.HashMap;
import java.util.Map;

/**
 * Управляет регистрацией и выполнением команд.
 * Реализует паттерн Command.
 */
public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    /**
     * Регистрирует новую команду в системе
     * @param commandName название команды (ключ для поиска)
     * @param command объект команды, реализующий интерфейс Command
     */
    public void registerCommand(String commandName, Command command) {
        commands.put(commandName, command);
    }

    /**
     * Выполняет команду по её имени из массива аргументов
     * @param args массив аргументов, где args[0] - имя команды
     */
    public void executeCommand(String[] args) {

        if (args.length == 0 || args[0].trim().isEmpty()) {
            return;
        }

        String commandName = args[0].toLowerCase();

        Command command = commands.get(commandName);

        if (command == null) {
            System.out.println("Неизвестная команда: '" + commandName + "'. Введите 'help' для справки.");
        } else {
            try {
//                command.execute(args);
            } catch (Exception e) {
            System.out.println("Произошла ошибка при выполнении команды: " + e.getMessage());
            }
        }
    }
    /**
     * Возвращает карту всех зарегистрированных команд
     * @return Map с командами (ключ - имя команды)
     */
    public Map<String, Command> getCommands() {
        return commands;
    }
}
