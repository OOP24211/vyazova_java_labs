package com.example.watchtogether.controller;

import com.example.watchtogether.model.RoomState;
import com.example.watchtogether.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RoomHttpController {

    private final RoomService roomService;

    public RoomHttpController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public Collection<RoomState> listRooms() {
        return roomService.getAllRooms();
    }

    @PostMapping
    public RoomState createRoom() {
        String randomId = UUID.randomUUID().toString().substring(0, 8);
        return roomService.getOrCreateRoom(randomId);
    }
}