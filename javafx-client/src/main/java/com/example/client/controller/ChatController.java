package com.example.client.controller;

import com.example.client.service.ApiService;
import com.example.client.service.WebSocketService;
import com.example.client.session.AppSession;
import com.example.client.util.SceneManager;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatController {

    @FXML private TextField messageField;
    @FXML private TextArea chatArea;
    @FXML private Label titleLabel;

    private final ApiService apiService = new ApiService();
    private WebSocketService ws;
    private final Set<String> processed = new HashSet<>();

    @FXML
    public void initialize() {
        titleLabel.setText("Chat - Room #" + AppSession.getRoomId());

        ws = new WebSocketService(
                AppSession.getUsername(),
                AppSession.getRoomId().intValue(),
                this
        );

        ws.connect();
        loadHistory();

        chatArea.setOnMouseClicked(event -> {
            String selectedText = chatArea.getSelectedText();
            if (selectedText != null && !selectedText.isEmpty()) {
                String url = extractUrl(selectedText);
                if (url != null) {
                    openFileInBrowser(url);
                }
            } else {
                int caretPosition = chatArea.getCaretPosition();
                String text = chatArea.getText();

                String url = findUrlAtPosition(text, caretPosition);
                if (url != null) {
                    openFileInBrowser(url);
                }
            }
        });
    }

    private String extractUrl(String text) {
        Pattern pattern = Pattern.compile("(http://localhost:8080/uploads/[^\\s\\n]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String findUrlAtPosition(String text, int position) {
        Pattern pattern = Pattern.compile("(http://localhost:8080/uploads/[^\\s\\n]+)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (position >= start - 5 && position <= end + 5) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private void openFileInBrowser(String fileUrl) {
        System.out.println("Opening file: " + fileUrl);

        try {
            String encodedUrl = fileUrl.replace(" ", "%20");
            encodedUrl = encodedUrl.replace("~", "%7E");
            encodedUrl = encodedUrl.replace("$", "%24");

            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", encodedUrl});
            } else if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", encodedUrl});
            } else {
                Runtime.getRuntime().exec(new String[]{"xdg-open", encodedUrl});
            }
            System.out.println("Browser opened successfully!");
        } catch (Exception e) {
            System.err.println("Failed to open browser: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void sendMessage() {
        String text = messageField.getText();
        if (text == null || text.isBlank()) return;

        ws.sendMessage(text);
        messageField.clear();
    }

    @FXML
    private void sendFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file to send");

        Stage stage = (Stage) chatArea.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            System.out.println("Sending file: " + selectedFile.getName());
            chatArea.appendText("Uploading: " + selectedFile.getName() + "...\n");

            boolean success = apiService.uploadFile(
                    selectedFile,
                    AppSession.getRoomId().intValue(),
                    AppSession.getUsername()
            );

            if (success) {
                chatArea.appendText("File uploaded!\n");
                ws.sendFileNotification(selectedFile.getName());
            } else {
                chatArea.appendText("Failed to upload: " + selectedFile.getName() + "\n");
            }
        }
    }

    @FXML
    private void backToRooms() {
        if (ws != null) {
            ws.close();
        }
        chatArea.clear();
        processed.clear();
        SceneManager.switchScene("/view/rooms.fxml");
    }

    public void addFileMessage(String username, String fileName, String fileUrl, String fileType) {
        String fullUrl = "http://localhost:8080" + fileUrl;
        String displayText;
        if ("image".equals(fileType)) {
            displayText = username + " sent an image: " + fileName + "\n   " + fullUrl + "\n   (кликните на ссылку чтобы открыть)\n";
        } else {
            displayText = username + " sent a file: " + fileName + "\n   " + fullUrl + "\n   (кликните на ссылку чтобы скачать)\n";
        }
        chatArea.appendText(displayText);
    }

    public void addMessage(String username, String text, String timestamp) {
        String id = timestamp + "_" + username + "_" + text;
        if (processed.contains(id)) return;
        processed.add(id);
        chatArea.appendText(username + ": " + text + "\n");
    }

    private void loadHistory() {
        String json = apiService.getHistory(AppSession.getRoomId().intValue());

        if (json == null || json.isBlank()) {
            System.out.println("History is empty or null");
            return;
        }

        try {
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            JsonNode arr = mapper.readTree(json);

            if (!arr.isArray()) {
                System.out.println("History response is not array: " + json);
                return;
            }

            for (JsonNode msg : arr) {
                String username = msg.has("sender") && msg.get("sender").has("username")
                        ? msg.get("sender").get("username").asText()
                        : msg.has("username") ? msg.get("username").asText()
                        : "unknown";

                if (msg.has("fileUrl") && !msg.get("fileUrl").isNull()) {
                    String fileName = msg.has("fileName") ? msg.get("fileName").asText() : "file";
                    String fileUrl = msg.get("fileUrl").asText();
                    String fileType = msg.has("fileType") ? msg.get("fileType").asText() : "document";
                    addFileMessage(username, fileName, fileUrl, fileType);
                } else {
                    String text = msg.has("text") ? msg.get("text").asText() : "";
                    String timestamp = msg.has("timestamp") ? msg.get("timestamp").asText()
                            : String.valueOf(System.currentTimeMillis());
                    addMessage(username, text, timestamp);
                }
            }

        } catch (Exception e) {
            System.err.println("Error parsing history: " + e.getMessage());
            e.printStackTrace();
        }
    }
}