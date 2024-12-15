package org.august.aminoAuthorizator;

import okhttp3.WebSocket;
import org.august.AminoApi.dto.intermediate.ProxySettings;
import org.august.AminoApi.dto.response.AuthDto;
import org.august.AminoApi.services.ClientApi;
import org.august.aminoAuthorizator.amino.WSRealization.websoket.WSSManager;
import org.august.aminoAuthorizator.listeners.AuthListener;
import org.august.aminoAuthorizator.managers.AuthManager;
import org.august.aminoAuthorizator.managers.ConfigManager;
import org.august.aminoAuthorizator.managers.DataManager;
import org.august.aminoAuthorizator.util.ConfigKey;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AminoAuthorizator extends JavaPlugin {
    private static final AuthManager authManager = new AuthManager();
    private static DataManager dataManager;
    private static ConfigManager configManager;
    private static ClientApi aminoClient;
    private static WSSManager wssManager;
    private static AuthDto authDto;

    public static WSSManager getWssManager() {
        return wssManager;
    }
    public static ClientApi getAminoClient() { return aminoClient; }
    public static AuthDto getAuthDto() {
        return authDto;
    }
    public static AuthManager getAuthManager() {return authManager;}
    public static DataManager getDataManager() { return dataManager; }

    @Override
    public void onEnable() {
        try {
            // Инициализация
            this.checkInit();
            if (configManager.get(ConfigKey.PROXY_ENABLED)){
                ProxySettings proxySettings = new ProxySettings(
                        configManager.get(ConfigKey.PROXY_PORT),
                        configManager.get(ConfigKey.PROXY_HOST)
                );
                aminoClient.setProxy(proxySettings);
            }

            aminoClient.setDeviceId(configManager.get(ConfigKey.AUTH_DEVICE_ID));

            // Авторизация
            AuthorizationService authorizationService = new AuthorizationService(aminoClient, 3, 30);
            authDto = authorizationService.authorize(
                    configManager.get(ConfigKey.AUTH_EMAIL),
                    configManager.get(ConfigKey.AUTH_PASSWORD)
            );

            getLogger().info("Login successful! User: " + authDto.getUserProfile().getNickname());

            // WebSocket
            wssManager = new WSSManager(aminoClient);
            WebSocket socket = wssManager.connect();
            wssManager.heartbeat(socket);

            // Слушатели и данные
            Bukkit.getPluginManager().registerEvents(new AuthListener(authManager), this);
            dataManager.loadData();
            getLogger().info("Amino Authorizator activated!!");
        } catch (Exception e) {
            getLogger().severe("Failed to enable plugin: " + e.getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this); // Выключение при ошибке
        }
    }

    public void checkInit() {
        configManager = new ConfigManager(getDataFolder().getPath());
        dataManager = new DataManager(getDataFolder().getPath(), configManager.get(ConfigKey.DATA_FILE_NAME));

        // Настройка Amino
        aminoClient = new ClientApi();

        if (!(boolean) configManager.get(ConfigKey.PLUGIN_ENABLED)) {
            System.out.println("\n\n" +
                    "###############################################################\n" +
                    "                     \u001B[31mПЛАГИН ОТКЛЮЧЕН\u001B[0m                       \n" +
                    "###############################################################\n" +
                    "Ваш плагин отключен! Для его активации выполните следующие шаги:\n\n" +
                    "1. Откройте папку с конфигурацией плагина: \n" +
                    "   \u001B[34m" + getDataFolder().getPath() + "\u001B[0m\n\n" +
                    "2. В файле \u001B[36mconfig.yml\u001B[0m заполните следующие поля:\n" +
                    "   - \u001B[32mauth.email\u001B[0m: ваш email от аккаунта Амино\n" +
                    "   - \u001B[32mauth.password\u001B[0m: ваш пароль от аккаунта Амино\n\n" +
                    "3. После этого установите параметр \u001B[33mplugin.enabled\u001B[0m в \u001B[32mtrue\u001B[0m.\n\n" +
                    "4. Перезапустите сервер.\n\n" +
                    "###############################################################\n\n"
            );

            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        dataManager.saveData();
        getLogger().info("Amino Authorizator deactivated!!1");
    }
}
