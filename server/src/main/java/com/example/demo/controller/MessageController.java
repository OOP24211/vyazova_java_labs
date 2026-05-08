package com.example.demo.controller;

import com.example.demo.model.Message;
import com.example.demo.model.Room;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.RoomRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;

    public MessageController(MessageRepository messageRepository, RoomRepository roomRepository) {
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
    }

    @GetMapping
    public List<Message> getMessages(@RequestParam Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return messageRepository.findByRoomOrderByTimestampAsc(room);
    }

    @GetMapping("/recent")
    public List<Message> getRecentMessages(
            @RequestParam Long roomId,
            @RequestParam(defaultValue = "50") int limit) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        List<Message> messages = messageRepository.findByRoomOrderByTimestampAsc(room);

        if (messages.size() > limit) {
            return messages.subList(messages.size() - limit, messages.size());
        }
        return messages;
    }
}
