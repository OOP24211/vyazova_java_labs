package com.example.watchtogether.controller;

import com.example.watchtogether.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/room.chat")
    public void handleChatMessage(ChatMessage message) {
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId() + "/chat", message);
        System.out.println("CHAT [" + message.getRoomId() + "] " + message.getSender() + ": " + message.getContent());
    }
}