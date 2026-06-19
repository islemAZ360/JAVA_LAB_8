package main.java.client.gui.core;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private Properties properties = new Properties();

    public ConfigLoader(String configPath) {
//        Absolute from src.main.resources
//        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configPath)) {
//        Relative from thís class
        try (InputStream input = ConfigLoader.class.getResourceAsStream(configPath)) {
            if (input == null) {
                System.out.println("Не найдено " + configPath);
                return;
            }
            // Загружка в память
            properties.load(input);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
}
