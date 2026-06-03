package com.example.watchtogether.model;

public class RoomState {
    private String id;
    private boolean playing;
    private float currentTime;
    private String videoId;

    public RoomState(String id) {
        this.id = id;
        this.playing = false;
        this.currentTime = 0.0f;
        this.videoId = "dQw4w9WgXcQ";
    }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getId() { return id; }
    public boolean isPlaying() { return playing; }
    public void setPlaying(boolean playing) { this.playing = playing; }
    public float getCurrentTime() { return currentTime; }
    public void setCurrentTime(float currentTime) { this.currentTime = currentTime; }
}