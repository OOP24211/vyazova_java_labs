package com.example.watchtogether.service;

import com.example.watchtogether.model.RoomState;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

    private final ConcurrentHashMap<String, RoomState> rooms = new ConcurrentHashMap<>();

    public Collection<RoomState> getAllRooms() {
        return rooms.values();
    }

    public RoomState getOrCreateRoom(String roomId) {
        return rooms.computeIfAbsent(roomId, id -> new RoomState(id));
    }

    public void updateState(String roomId, boolean playing, float time) {
        RoomState room = rooms.get(roomId);
        if (room != null) {
            room.setPlaying(playing);
            room.setCurrentTime(time);
        }
    }

    public void updateVideo(String roomId, String newVideoId) {
        RoomState room = rooms.get(roomId);
        if (room != null) {
            room.setVideoId(newVideoId);
            room.setCurrentTime(0.0f);
            room.setPlaying(false);
        }
    }

    public void deleteRoom(String roomId) {
        rooms.remove(roomId);
    }
}