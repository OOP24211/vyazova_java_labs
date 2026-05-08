package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private LocalDateTime timestamp;

    private String fileUrl;
    private String fileType;
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}