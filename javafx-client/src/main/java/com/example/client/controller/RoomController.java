package com.example.client.controller;

import com.example.client.service.ApiService;
import com.example.client.session.AppSession;
import com.example.client.util.SceneManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class RoomController {

    private final ApiService apiService = new ApiService();

    @FXML private TextField roomNameField;
    @FXML private ListView<String> roomsList;

    @FXML
    private void createRoom() {
        String name = roomNameField.getText();

        if (name == null || name.isBlank()) {
            System.out.println("Room name cannot be empty");
            return;
        }

        String response = apiService.createRoom(name);
        System.out.println("ROOM CREATED: " + response);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response);
            long roomId = node.get("id").asLong();
            AppSession.setRoomId(roomId);
            System.out.println("Created room with ID: " + roomId);
            SceneManager.switchScene("/view/chat.fxml");
        } catch (Exception e) {
            System.out.println("Error parsing room ID: " + e.getMessage());
            e.printStackTrace();
            AppSession.setRoomId(1L);
            SceneManager.switchScene("/view/chat.fxml");
        }
    }

    @FXML
    private void joinRoom() {
        String selected = roomsList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println("No room selected");
            return;
        }

        try {
            long roomId;
            if (selected.contains("ID:")) {
                String idStr = selected.replaceAll(".*ID:\\s*(\\d+).*", "$1");
                roomId = Long.parseLong(idStr);
            } else {
                System.out.println("Room list should display IDs");
                return;
            }

            AppSession.setRoomId(roomId);
            SceneManager.switchScene("/view/chat.fxml");

        } catch (Exception e) {
            System.out.println("Error parsing room ID: " + e.getMessage());
        }
    }

    @FXML
    private void loadRooms() {
        String response = apiService.getRooms();
        System.out.println("Raw rooms response: " + response);

        if (response != null && !response.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rooms = mapper.readTree(response);

                roomsList.getItems().clear();

                if (rooms.isArray()) {
                    for (JsonNode room : rooms) {
                        String name = room.get("name").asText();
                        Long id = room.get("id").asLong();
                        roomsList.getItems().add(name + " (ID: " + id + ")");
                        System.out.println("Added room: " + name + " (ID: " + id + ")");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error parsing rooms: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No rooms received from server");
        }
    }

    @FXML
    public void initialize() {
        loadRooms();
    }
}