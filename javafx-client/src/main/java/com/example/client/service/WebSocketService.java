package com.example.client.service;

import com.example.client.controller.ChatController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocketService {

    private WebSocketClient client;
    private final String username;
    private final int roomId;
    private final ChatController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    public WebSocketService(String username, int roomId, ChatController controller) {
        this.username = username;
        this.roomId = roomId;
        this.controller = controller;
    }

    public void connect() {
        try {
            client = new WebSocketClient(new URI("ws://localhost:8080/chat")) {

                @Override
                public void onOpen(ServerHandshake handshake) {
                    sendJoin();
                }

                @Override
                public void onMessage(String message) {
                    Platform.runLater(() -> {
                        try {
                            var json = mapper.readTree(message);
                            String type = json.get("type").asText();

                            if ("message".equals(type)) {
                                String username = json.get("username").asText();
                                String text = json.get("text").asText();
                                String timestamp = json.has("timestamp")
                                        ? json.get("timestamp").asText()
                                        : String.valueOf(System.currentTimeMillis());

                                controller.addMessage(username, text, timestamp);
                            } else if ("file".equals(type)) {
                                String username = json.get("username").asText();
                                String fileName = json.get("fileName").asText();
                                String fileUrl = json.get("fileUrl").asText();
                                String fileType = json.has("fileType") ? json.get("fileType").asText() : "document";

                                controller.addFileMessage(username, fileName, fileUrl, fileType);
                            }

                        } catch (Exception e) {
                            System.out.println("WS parse error: " + e.getMessage());
                        }
                    });
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("WS closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };

            client.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }

    private void sendJoin() {
        try {
            ObjectNode json = mapper.createObjectNode();
            json.put("type", "join");
            json.put("username", username);
            json.put("roomId", roomId);

            client.send(mapper.writeValueAsString(json));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String text) {
        try {
            ObjectNode json = mapper.createObjectNode();
            json.put("type", "message");
            json.put("username", username);
            json.put("roomId", roomId);
            json.put("text", text);

            client.send(mapper.writeValueAsString(json));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFileNotification(String fileName) {
        try {
            ObjectNode json = mapper.createObjectNode();
            json.put("type", "file");
            json.put("username", username);
            json.put("roomId", roomId);
            json.put("fileName", fileName);
            json.put("fileUrl", "/uploads/" + fileName);
            json.put("fileType", fileName.matches(".*\\.(png|jpg|jpeg|gif)$") ? "image" : "document");

            client.send(mapper.writeValueAsString(json));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}