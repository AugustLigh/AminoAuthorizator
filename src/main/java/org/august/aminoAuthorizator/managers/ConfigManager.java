package org.august.aminoAuthorizator.managers;

import org.august.aminoAuthorizator.util.ConfigKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final File configFile;
    private FileConfiguration config;

    public ConfigManager(String pluginFolder) {
        this.configFile = new File(pluginFolder, "config.yml");
        this.loadDefaultConfig();
        this.loadConfig();
    }

    public void loadDefaultConfig() {
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs(); // Создаёт папки, если их нет
                configFile.createNewFile();         // Создаёт файл
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.config = YamlConfiguration.loadConfiguration(configFile);

            // Установка значений по умолчанию из ConfigKey
            for (ConfigKey key : ConfigKey.values()) {
                config.set(key.getKey(), key.getDefaultValue());
            }
            saveConfig();
        }
    }

    // Загрузка конфигурации
    public void loadConfig() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    // Сохранение конфигурации
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    public <T> T get(ConfigKey key) {
        Object value = config.get(key.getKey(), key.getDefaultValue());
        try {
            return (T) value;
        } catch (ClassCastException e) {
            System.err.println("Ошибка приведения типа для ключа '" + key.getKey() + "'. Используется значение по умолчанию.");
            return (T) key.getDefaultValue();
        }
    }

    public void set(ConfigKey key, Object value) {
        config.set(key.getKey(), value);
        saveConfig();
    }
}