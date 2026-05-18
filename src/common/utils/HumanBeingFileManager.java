package common.utils;

import common.HumanBeingReader;
import common.models.HumanBeing;
import server.CollectionManager;

import java.io.*;
import java.util.Collection;
import java.util.Scanner;

public class HumanBeingFileManager extends FileManager implements HandleHumanBeingFile {
    public HumanBeingFileManager(String filePath) {
        super(filePath);
    }

    public void readFileAndLoadHumanBeing(CollectionManager collectionManager) {
        File file = this.readFile();

        if (file == null) {
            System.out.println("Коллекция не загружена, так как файл отсутствует.");
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                try {
                    long currentId = HumanBeingReader.extractIdFromLine(line);

                    if (collectionManager.getHumanById(currentId) == null) {
                        HumanBeing human = HumanBeingReader.convertLine(line);
                        collectionManager.add(human);
                        human.setValueHumanCount(collectionManager.getMaxId());
                    } else {
                        System.out.println("Id " + currentId + " already existed!");
                    }

                } catch (Exception e) {
                    System.out.println("Ошибка при чтении строки: " + e.getMessage());
                }
            }

            System.out.println("Коллекция успешно загружена из файла.");
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: файл не найден.");
        }
    }

    /**
     * Сохраняет один объект HumanBeing в файл (режим добавления)
     */
    public void save(HumanBeing human) {
        // Формируем строку CSV
        String csvLine = HumanBeingReader.extractInfo(human);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.getFilePath(), true))) {
            writer.write(csvLine);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }

    /**
     * Сохраняет один объект и выводит сообщение
     */
    public void saveOne(HumanBeing human) {
        this.save(human);
        System.out.println("Human info успешно сохранена в файл.");
    }

    /**
     * Сохраняет всю коллекцию в файл (перезаписывает файл)
     */
    public void saveAll(Collection<HumanBeing> collection) {
        // Kiểm tra thư mục cha có tồn tại không
        File file = new File(this.getFilePath());
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs(); // Tạo thư mục nếu chưa tồn tại
        }

        // Sử dụng try-with-resources để tự động đóng writer
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.getFilePath(), false))) {
            // false = перезаписать файл, не добавлять

            // Ghi header (nếu muốn)
            // writer.write(String.join(",", Const.FILEHEADER));
            // writer.newLine();

            // Ghi từng element trong collection
            for (HumanBeing human : collection) {
                String csvLine = HumanBeingReader.extractInfo(human);
                writer.write(csvLine);
                writer.newLine();
            }

            writer.flush();
            System.out.println("Коллекция успешно сохранена в файл. Всего элементов: " + collection.size());

        } catch (IOException e) {
            System.out.println("Ошибка при сохранении коллекции в файл: " + e.getMessage());
        }
    }
}
