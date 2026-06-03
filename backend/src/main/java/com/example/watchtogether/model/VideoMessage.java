package com.example.watchtogether.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoMessage {
    private String roomId;
    private String type;
    private float time;
    private String videoId;
}
