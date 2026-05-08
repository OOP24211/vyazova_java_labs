#  JavaFX Chat — клиент-серверный мессенджер с поддержкой файлов

Многопользовательский чат с комнатами, историей сообщений и возможностью обмениваться файлами.

Клиент написан на **JavaFX + Maven**, сервер — на **Spring Boot + Gradle** с использованием **WebSocket** и **H2 Database**.

---

# 📌 Возможности

## 👤 Пользователи и авторизация

- Регистрация новых пользователей
- Вход по логину и паролю
- Хранение пользователей в базе данных H2
- Сохранение текущей сессии пользователя

---

## 🏠 Комнаты

- Создание новых комнат
- Просмотр списка существующих комнат
- Подключение к выбранной комнате
- Автоматическая загрузка истории сообщений

---

## 💬 Общение

- Обмен сообщениями в реальном времени через WebSocket
- История сообщений сохраняется в базе данных
- Отображение времени отправки сообщений
- Уведомления о входе пользователей в комнату

---

## 📎 Работа с файлами

- Отправка файлов в чат
- Поддержка изображений, PDF, DOCX и других форматов
- Ссылки на загруженные файлы отображаются в чате
- Открытие файлов через браузер

---

# 🛠️ Используемые технологии

## Клиент

- Java 21
- JavaFX 17
- Maven
- Java-WebSocket
- Jackson

## Сервер

- Spring Boot 3
- Spring WebSocket
- Spring Data JPA
- H2 Database
- Gradle

---

# 🚀 Сборка проекта и запуск

# 1️⃣  Сборка проекта:

```bash
cd chat-server
./gradlew build
```

#  Запуск сервера

Перейдите в папку сервера:

```bash
cd server
```

Запустите сервер:

```bash
./gradlew bootRun
```

## Запуск через IntelliJ IDEA

Запустите класс:

```text
DemoApplication.java
```

---

После успешной сборки `.jar` файл появится в:

```text
chat-server/build/libs/
```

---

## WebSocket endpoint

```text
ws://localhost:8080/chat
```

---

## H2 Console

```text
http://localhost:8080/h2-console
```

---

# 2️⃣ Сборка и запуск клиента

Перейдите в папку клиента:

```bash
cd javafx-client
```

Соберите проект:

```bash
mvn clean package
```

Запустите приложение:

```bash
mvn clean javafx:run
```

---

## Запуск через IntelliJ IDEA

Запустите класс:

```text
MainApp.java
```

---

После сборки `.jar` файл появится в:

```text
javafx-client/target/
```

---

# 👤 Авторизация

После запуска клиента откроется окно входа.

## Регистрация

1. Введите username
2. Введите password
3. Нажмите Register

Пользователь будет сохранён в базе данных.

---

## Вход

1. Введите username и password
2. Нажмите Login

После успешного входа откроется окно комнат.

---

# 🏠 Работа с комнатами

## Создание комнаты

Введите название комнаты и нажмите:

```text
Create Room
```

---

## Обновление списка

Нажмите:

```text
Refresh
```

---

## Вход в комнату

1. Выберите комнату из списка
2. Нажмите:

```text
Join Room
```

---

# 💬 Работа с чатом

## Отправка сообщения

Введите текст и нажмите:

```text
Send
```

или клавишу:

```text
Enter
```

---

## Загрузка истории

Нажмите:

```text
Load History
```

Сообщения будут загружены из базы данных H2.

---

## Отправка файлов

Нажмите:

```text
Send File
```

Выберите файл — ссылка автоматически появится в чате.

---

# 📂 Структура проекта

## Сервер (`chat-server`)

```text
chat-server/
├── src/main/java/com/example/demo/
│
├── config/
│   ├── WebSocketConfig
│   └── WebConfig
│
├── controller/
│   ├── UserController
│   ├── RoomController
│   ├── MessageController
│   ├── FileController
│   └── ChatWebSocketHandler
│
├── model/
│   ├── User
│   ├── Room
│   └── Message
│
├── repository/
│   ├── UserRepository
│   ├── RoomRepository
│   └── MessageRepository
│
├── uploads/
│
├── build.gradle
└── settings.gradle
```

---

## Клиент (`javafx-client`)

```text
javafx-client/
├── src/main/java/com/example/client/
│
├── controller/
│   ├── LoginController
│   ├── RoomController
│   └── ChatController
│
├── service/
│   ├── ApiService
│   └── WebSocketService
│
├── session/
│   └── AppSession
│
├── util/
│   └── SceneManager
│
├── MainApp.java
│
├── src/main/resources/view/
│   ├── login.fxml
│   ├── rooms.fxml
│   └── chat.fxml
│
└── pom.xml
```

---

# 🗃️ Хранение данных

## База данных

Используется:

```text
H2 Database
```

Сообщения и пользователи сохраняются автоматически.

---

## Загруженные файлы

Файлы сохраняются в папку:

```text
uploads/
```

---

# 🧪 Что можно протестировать

## Одновременная работа нескольких пользователей

- Запустите два клиента
- Войдите под разными аккаунтами
- Подключитесь к одной комнате
- Проверьте обмен сообщениями в реальном времени

---

## История сообщений

1. Отправьте сообщения
2. Перезапустите клиент
3. Снова войдите в комнату
4. Нажмите:

```text
Load History
```

Сообщения должны загрузиться из базы данных.

---

## Передача файлов

- Изображения открываются в браузере
- PDF и DOCX скачиваются автоматически

---

# ⚠️ Возможные проблемы

## JavaFX runtime components are missing

Добавьте VM options:

```text
--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
```

---

## WebSocket не подключается

Проверьте:

```text
ws://localhost:8080/chat
```

И убедитесь, что сервер запущен.

---

## Ошибка загрузки истории

Проверьте endpoint:

```text
GET /messages?roomId=1
```

---

## Файлы не открываются

Проверьте настройку `WebConfig` на сервере и папку:

```text
uploads/
```