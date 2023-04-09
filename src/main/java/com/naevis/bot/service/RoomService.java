package com.naevis.bot.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.naevis.bot.model.Room;
import com.naevis.bot.model.AppUser;
import com.naevis.bot.repository.AppUserRepository;
import com.naevis.bot.repository.RoomRepository;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final AppUserRepository appUserRepository;

    public RoomService(RoomRepository roomRepository, AppUserRepository appUserRepository) {
        this.roomRepository = roomRepository;
        this.appUserRepository = appUserRepository;
    }

    public Room createRoom(AppUser user, String name) {
        Room room = Room.builder()
                .name(name)
                .owner(user)
                .joinedUsers(Collections.singletonList(user))
                .build();

        user.setRooms(Collections.singletonList(room));
        Room result = roomRepository.save(room);
        appUserRepository.save(user);
        return result;
    }
}
