package common.utils;

import server.CollectionManager;

import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Управляет чтением и выполнением скриптов из файла.
 * Наследует FileManager, реализует HandleCommandFile.
 */
public class CommandFileManager extends FileManager implements HandleCommandFile {

    /**
     * Создаёт менеджер для работы с файлом скриптов
     * @param filePath путь к файлу со скриптами
     */
    public CommandFileManager(String filePath) {
        super(filePath);
    }

    /**
     * Читает файл построчно и выполняет каждую команду через CommandManager
     * @param collectionManager менеджер коллекции для передачи командам
     * @param commandManager менеджер команд для выполнения
     * @param humanBeingFileManager менеджер файлов данных
     */
    public void readFileAndRunScripts(
            CollectionManager collectionManager,
            CommandManager commandManager,
            HumanBeingFileManager humanBeingFileManager
    ) {
        try (Scanner scanner = new Scanner(this.readFile())) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                // игнорировать пустую строку
                if (line.isEmpty()) continue;

                try {
                    System.out.println(">>> Reading and executing line: " + line);

                    // Отправляет команду в CommandManager для обработки
                    commandManager.executeCommand(line.split("\\s+"));

                } catch (Exception e) {
                    System.out.println("Ошибка при выполнении команды: " + e.getMessage());
                }
            }

            System.out.println("Скрипт успешно выполнен.");

        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: файл не найден.");
        }
    }
}
