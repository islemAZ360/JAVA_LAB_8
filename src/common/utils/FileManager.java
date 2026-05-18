package common.utils;

import java.io.*;

/**
 * Класс для работы с файлом (CSV).
 * Отвечает за чтение данных при запуске и сохранение коллекции.
 */
public abstract class FileManager {
    private final String filePath;

    /**
     * Конструктор принимает путь к файлу из Main.
     */
    public FileManager(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Считывает данные из файла и добавляет их в коллекцию.
     * Использует Scanner для чтения.
     */
    public File readFile() {
        File file = new File(filePath);

        int currentMaxId = 0;

        // Проверяем, существует ли файл перед чтением
        if (!file.exists()) {
            System.out.println("Файл не найден. Будет создан новый при сохранении.");
            return null;
        }
        return file;
    }

    /**
     * Getter для пути к файлу
     */
    public String getFilePath() {
        return filePath;
    }
}