package com.example.watchtogether.controller;

import com.example.watchtogether.model.VideoMessage;
import com.example.watchtogether.model.RoomState;
import com.example.watchtogether.service.RoomService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Controller
public class VideoController {

    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, Set<String>> roomSessions = new ConcurrentHashMap<>();

    private final Map<String, String> sessionToRoom = new ConcurrentHashMap<>();

    public VideoController(RoomService roomService, SimpMessagingTemplate messagingTemplate) {
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/room.join")
    public void joinRoom(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        String roomId = payload.get("roomId");
        if (roomId == null) return;

        String sessionId = headerAccessor.getSessionId();
        if (sessionId == null) return;

        roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(sessionId);
        sessionToRoom.put(sessionId, roomId);

        broadcastOnline(roomId);
    }

    @MessageMapping("/room.leave")
    public void leaveRoom(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        String roomId = payload.get("roomId");
        if (roomId == null) return;

        String sessionId = headerAccessor.getSessionId();
        handleUserLeave(roomId, sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        if (sessionId != null) {
            String roomId = sessionToRoom.remove(sessionId);
            if (roomId != null) {
                handleUserLeave(roomId, sessionId);
            }
        }
    }

    private void handleUserLeave(String roomId, String sessionId) {
        Set<String> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(sessionId);

            if (sessions.isEmpty()) {
                roomSessions.remove(roomId);
                roomService.deleteRoom(roomId);
            } else {
                broadcastOnline(roomId);
            }
        }
    }

    private void broadcastOnline(String roomId) {
        Set<String> sessions = roomSessions.get(roomId);
        int online = (sessions != null) ? sessions.size() : 0;

        Map<String, Object> response = new ConcurrentHashMap<>();
        response.put("roomId", roomId);
        response.put("onlineCount", online);

        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/online", response);
    }


    @MessageMapping("/room.play")
    public void play(VideoMessage message) {
        boolean isPlaying = message.getType().equals("PLAY");
        roomService.updateState(message.getRoomId(), isPlaying, message.getTime());
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
    }

    @MessageMapping("/room.getState")
    public void getState(VideoMessage message) {
        RoomState state = roomService.getOrCreateRoom(message.getRoomId());

        VideoMessage reply = new VideoMessage();
        reply.setRoomId(message.getRoomId());
        reply.setType(state.isPlaying() ? "PLAY" : "PAUSE");
        reply.setTime(state.getCurrentTime());
        reply.setVideoId(state.getVideoId());

        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), reply);
    }

    @MessageMapping("/room.changeVideo")
    public void changeVideo(VideoMessage message) {
        roomService.updateVideo(message.getRoomId(), message.getVideoId());

        VideoMessage reply = new VideoMessage();
        reply.setRoomId(message.getRoomId());
        reply.setType("CHANGE_VIDEO");
        reply.setVideoId(message.getVideoId());
        reply.setTime(0.0f);

        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), reply);
        System.out.println("Room [" + message.getRoomId() + "] changed video to: " + message.getVideoId());
    }
}