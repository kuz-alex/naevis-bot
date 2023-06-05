package com.naevis.bot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.naevis.bot.dto.RoomDto;
import com.naevis.bot.model.AppUser;
import com.naevis.bot.model.Room;
import com.naevis.bot.model.TimeEntry;
import com.naevis.bot.dto.TimeEntryDto;
import com.naevis.bot.repository.AppUserRepository;
import com.naevis.bot.repository.RoomRepository;
import com.naevis.bot.repository.TimeEntryRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomsController {
    private final TimeEntryRepository timeEntryRepository;
    private final AppUserRepository appUserRepository;
    private final RoomRepository roomRepository;

    public RoomsController(TimeEntryRepository timeEntryRepository, AppUserRepository appUserRepository,
                           RoomRepository roomRepository) {
        this.timeEntryRepository = timeEntryRepository;
        this.appUserRepository = appUserRepository;
        this.roomRepository = roomRepository;
    }

    @GetMapping("/{roomCode}/time-entries")
    public List<TimeEntryDto> getSessionsByRoom(@PathVariable String roomCode) {
        List<AppUser> users = appUserRepository.findByRoomCode(roomCode);

        List<TimeEntryDto> result = new ArrayList<>();
        for (AppUser user : users) {
            List<TimeEntry> sessions = timeEntryRepository.findByUserId(user.getId());

            for (TimeEntry session : sessions) {
                result.add(TimeEntryDto.builder()
                        .id(session.getId())
                        .name(session.getName())
                        .durationMin(session.getDurationMin())
                        .telegramUser(user.getFullName() + " @" + user.getUserName())
                        .startedAt(session.getStartedAt())
                        .stoppedAt(session.getStoppedAt())
                        .build()
                );
            }
        }
        return result;
    }

    @GetMapping("/{userId}")
    public List<RoomDto> getRoomsByUserId(@PathVariable String userId) {
        List<Room> rooms = roomRepository.findRoomsByUserId(Long.valueOf(userId));
        List<RoomDto> roomDtos = rooms.stream()
                .map((room) -> {
                    return RoomDto.builder()
                            .id(room.getId())
                            .name(room.getName())
                            .code(room.getCode())
                            .createdAt(room.getCreatedAt().toInstant())
                            .build();
                })
                .collect(Collectors.toList());

        return roomDtos;
    }
}
