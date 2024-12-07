# Amino Authorizator Plugin

Amino Authorizator — это плагин для Minecraft, который предоставляет механизм аутентификации пользователей через API Amino. Он включает в себя настройки для управления входом через email, пароли, прокси-серверы и WebSocket-соединения.

## 🚀 Возможности

- **Аутентификация через Amino**: Логин через код от бота.
- **Поддержка прокси**: Легко настраиваемые параметры прокси-серверов.
- **WebSocket-соединение**: Подключение к серверу через WebSocket с настройками интервала heartbeat и задержки переподключения.
- **Автоматическое сохранение данных**: Настроенный интервал авто-сохранения данных.
- **Гибкие настройки**: Возможность настройки логирования, сообщений и других параметров через конфигурационный файл.

![IMG_3625](https://github.com/user-attachments/assets/354c12ae-f131-4987-9a44-9f1c7eb069dc)

## 📥 Установка

1. Скачайте последний релиз плагина.
2. Поместите файл `.jar` в папку `plugins` вашего сервера Minecraft.
3. Перезапустите сервер.

## ⚙️ Настройка

После установки плагина вам нужно настроить файл конфигурации `config.yml`.

### Конфигурационный файл

```yaml
plugin:
  enabled: false
  log_level: "INFO"

auth:
  email: "default_email@example.com"
  password: "default_password"
  device_id: "197E0C5D7EDB8394DB40D533D65338123CCBBB20DB05CB64192EE428D459D11D1DB143F1BF2CCB4C5C"
  max_login_attempts: 3
  login_timeout_seconds: 30

proxy:
  enabled: false
  host: "0.0.0.0"
  port: 0000
  type: "HTTP"

websocket:
  heartbeat_interval: 30000
  reconnect_delay: 5000

data:
  auto_save_interval: 600
  file_name: "data.json"

messages:
  login_success: "Авторизация прошла успешно! Добро пожаловать!"
  login_fail: "Ошибка авторизации. Проверьте email и пароль."
  proxy_error: "Ошибка соединения через прокси."
  websocket_error: "Ошибка соединения с WebSocket. Переподключение..."
