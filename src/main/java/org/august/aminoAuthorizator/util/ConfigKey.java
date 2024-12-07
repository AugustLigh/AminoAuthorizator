package org.august.aminoAuthorizator.util;

public enum ConfigKey {
    // Параметры плагина
    PLUGIN_ENABLED("plugin.enabled", false),
    PLUGIN_LOG_LEVEL("plugin.log_level", "INFO"),

    // Параметры авторизации
    AUTH_EMAIL("auth.email", "default_email@example.com"),
    AUTH_PASSWORD("auth.password", "default_password"),
    AUTH_DEVICE_ID("auth.device_id", "197E0C5D7EDB8394DB40D533D65338123CCBBB20DB05CB64192EE428D459D11D1DB143F1BF2CCB4C5C"),
    AUTH_MAX_LOGIN_ATTEMPTS("auth.max_login_attempts", 3),
    AUTH_LOGIN_TIMEOUT_SECONDS("auth.login_timeout_seconds", 30),

    // Параметры прокси
    PROXY_ENABLED("proxy.enabled", false),
    PROXY_HOST("proxy.host", "0.0.0.0"),
    PROXY_PORT("proxy.port", 0000),
    PROXY_TYPE("proxy.type", "HTTP"),

    // Параметры WebSocket
    WEBSOCKET_HEARTBEAT_INTERVAL("websocket.heartbeat_interval", 30000),
    WEBSOCKET_RECONNECT_DELAY("websocket.reconnect_delay", 5000),

    // Параметры данных
    DATA_AUTO_SAVE_INTERVAL("data.auto_save_interval", 600),
    DATA_FILE_NAME("data.file_name", "data.json"),

    // Сообщения
    MESSAGE_LOGIN_SUCCESS("messages.login_success", "Авторизация прошла успешно! Добро пожаловать!"),
    MESSAGE_LOGIN_FAIL("messages.login_fail", "Ошибка авторизации. Проверьте email и пароль."),
    MESSAGE_PROXY_ERROR("messages.proxy_error", "Ошибка соединения через прокси."),
    MESSAGE_WEBSOCKET_ERROR("messages.websocket_error", "Ошибка соединения с WebSocket. Переподключение...");

    private final String key;
    private final Object defaultValue;

    ConfigKey(String key, Object defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
