package com.example.watchtogether.service;

import com.example.watchtogether.model.RoomState;
import com.example.watchtogether.model.VideoMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SyncScheduler {

    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    public SyncScheduler(RoomService roomService, SimpMessagingTemplate messagingTemplate) {
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 5000)
    public void broadcastCurrentTime() {
        for (RoomState state : roomService.getAllRooms()) {
            if (state.isPlaying()) {
                float updatedTime = state.getCurrentTime() + 5.0f;
                roomService.updateState(state.getId(), true, updatedTime);

                VideoMessage syncMessage = new VideoMessage();
                syncMessage.setRoomId(state.getId());
                syncMessage.setType("SYNC");
                syncMessage.setTime(updatedTime);

                messagingTemplate.convertAndSend("/topic/room/" + state.getId(), syncMessage);
            }
        }
    }
}