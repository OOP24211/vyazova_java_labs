package com.example.demo.controller;

import com.example.demo.model.Message;
import com.example.demo.model.Room;
import com.example.demo.model.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public FileController(MessageRepository messageRepository,
                          UserRepository userRepository,
                          RoomRepository roomRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        try {
            Files.createDirectories(fileStorageLocation);
            System.out.println("Upload directory created at: " + fileStorageLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("roomId") Long roomId,
                                        @RequestParam("username") String username) {
        try {
            String originalName = file.getOriginalFilename();
            String contentType = file.getContentType();

            String fileName = UUID.randomUUID().toString() + "_" + originalName;
            Path targetPath = fileStorageLocation.resolve(fileName);

            Files.copy(file.getInputStream(), targetPath);
            System.out.println("File saved to: " + targetPath);

            String fileUrl = "/uploads/" + fileName;
            String fileType = contentType != null && contentType.startsWith("image") ? "image" : "document";

            User sender = userRepository.findByUsername(username).orElse(null);
            Room room = roomRepository.findById(roomId).orElse(null);

            if (sender == null || room == null) {
                return ResponseEntity.badRequest().body("User or room not found");
            }

            Message message = new Message();
            message.setText("[File: " + originalName + "]");
            message.setFileUrl(fileUrl);
            message.setFileType(fileType);
            message.setFileName(originalName);
            message.setSender(sender);
            message.setRoom(room);
            message.setTimestamp(LocalDateTime.now());
            messageRepository.save(message);

            System.out.println("File message saved: " + fileUrl);

            return ResponseEntity.ok(message);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error uploading file: " + e.getMessage());
        }
    }
}