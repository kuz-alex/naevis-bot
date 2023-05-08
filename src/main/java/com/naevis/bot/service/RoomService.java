package com.naevis.bot.service;

import java.util.Collections;

import com.naevis.bot.model.Room;
import com.naevis.bot.model.AppUser;
import com.naevis.bot.repository.RoomRepository;
import com.naevis.bot.util.RandomUtils;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createRoom(AppUser user, String name) {
        String roomCode = RandomUtils.generateRoomCode(5);

        Room room = Room.builder()
                .name(name)
                .code(roomCode)
                .owner(user)
                .joinedUsers(Collections.singletonList(user))
                .build();

        return roomRepository.save(room);
    }
}
