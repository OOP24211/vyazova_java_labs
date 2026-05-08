package com.example.demo.websocket;

import com.example.demo.model.Message;
import com.example.demo.model.Room;
import com.example.demo.model.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ObjectMapper objectMapper;

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sessionUsers = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(MessageRepository messageRepository,
                                UserRepository userRepository,
                                RoomRepository roomRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        logger.info("New connection established: {}", session.getId());
        System.out.println("Новое подключение! Всего сессий: " + sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        logger.info("Received message: {}", payload);

        try {
            ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(payload);
            String type = jsonNode.has("type") ? jsonNode.get("type").asText() : "message";

            if ("join".equals(type)) {
                handleJoin(session, jsonNode);
            } else if ("message".equals(type)) {
                handleMessage(session, jsonNode);
            } else if ("file".equals(type)) {
                handleFile(session, jsonNode);
            }
        } catch (Exception e) {
            logger.error("Error processing message: ", e);
            session.sendMessage(new TextMessage("Error processing message: " + e.getMessage()));
        }
    }

    private void handleJoin(WebSocketSession session, ObjectNode jsonNode) throws Exception {
        String username = jsonNode.get("username").asText();
        Long roomId = jsonNode.get("roomId").asLong();

        sessionUsers.put(session.getId(), username);

        ObjectNode notification = objectMapper.createObjectNode();
        notification.put("type", "user_joined");
        notification.put("username", username);
        notification.put("roomId", roomId);

        String jsonResponse = objectMapper.writeValueAsString(notification);
        broadcast(jsonResponse);

        sendUserList();
    }

    private void handleMessage(WebSocketSession session, ObjectNode jsonNode) throws Exception {
        Long roomId = jsonNode.get("roomId").asLong();
        String username = jsonNode.get("username").asText();
        String text = jsonNode.get("text").asText();

        User sender = userRepository.findByUsername(username).orElse(null);
        Room room = roomRepository.findById(roomId).orElse(null);

        if (sender == null || room == null) {
            session.sendMessage(new TextMessage("Error: User or room not found"));
            return;
        }

        Message msg = new Message();
        msg.setText(text);
        msg.setSender(sender);
        msg.setRoom(room);
        msg.setTimestamp(LocalDateTime.now());
        messageRepository.save(msg);

        ObjectNode response = objectMapper.createObjectNode();
        response.put("type", "message");
        response.put("id", msg.getId());
        response.put("roomId", roomId);
        response.put("username", username);
        response.put("text", text);
        response.put("timestamp", msg.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        System.out.println("Будет отправлено: " + response.toString());

        broadcast(objectMapper.writeValueAsString(response));
    }

    private void handleFile(WebSocketSession session, ObjectNode jsonNode) throws Exception {
        String username = jsonNode.get("username").asText();
        Long roomId = jsonNode.get("roomId").asLong();
        String fileName = jsonNode.get("fileName").asText();
        String fileUrl = jsonNode.get("fileUrl").asText();
        String fileType = jsonNode.has("fileType") ? jsonNode.get("fileType").asText() : "document";

        System.out.println("Файл отправлен: " + fileName + " от " + username);

        ObjectNode response = objectMapper.createObjectNode();
        response.put("type", "file");
        response.put("username", username);
        response.put("roomId", roomId);
        response.put("fileName", fileName);
        response.put("fileUrl", fileUrl);
        response.put("fileType", fileType);

        broadcast(objectMapper.writeValueAsString(response));
    }

    private void sendUserList() throws Exception {
        ObjectNode userList = objectMapper.createObjectNode();
        userList.put("type", "user_list");
        userList.put("users", objectMapper.writeValueAsString(sessionUsers.values()));
        broadcast(objectMapper.writeValueAsString(userList));
    }

    private void broadcast(String message) throws Exception {
        System.out.println("РАССЫЛАЕМ: " + message);
        System.out.println("Количество сессий: " + sessions.size());

        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(message));
                System.out.println("Отправлено клиенту: " + s.getId());
            } else {
                System.out.println("Сессия закрыта: " + s.getId());
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = sessionUsers.remove(session.getId());
        sessions.remove(session.getId());

        if (username != null) {
            ObjectNode notification = objectMapper.createObjectNode();
            notification.put("type", "user_left");
            notification.put("username", username);
            broadcast(objectMapper.writeValueAsString(notification));
            sendUserList();
        }

        logger.info("Connection closed: {}", session.getId());
    }
}