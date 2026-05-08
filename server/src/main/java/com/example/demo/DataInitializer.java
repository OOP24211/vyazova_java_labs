package com.example.demo;

import com.example.demo.model.Room;
import com.example.demo.model.User;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public DataInitializer(RoomRepository roomRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roomRepository.count() == 0) {
            Room room = new Room();
            room.setName("General");
            roomRepository.save(room);
            System.out.println("Created default room with ID: " + room.getId());
        }

        if (userRepository.findByUsername("test").isEmpty()) {
            User user = new User();
            user.setUsername("test");
            user.setPassword("test");
            userRepository.save(user);
            System.out.println("Created test user: test/test");
        }

        roomRepository.findAll().forEach(room -> {
            System.out.println("Room: " + room.getName() + " (ID: " + room.getId() + ")");
        });
    }
}