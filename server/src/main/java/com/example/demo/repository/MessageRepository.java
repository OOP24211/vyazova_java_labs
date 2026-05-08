package com.example.demo.repository;

import com.example.demo.model.Message;
import com.example.demo.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRoomOrderByTimestampAsc(Room room);
}