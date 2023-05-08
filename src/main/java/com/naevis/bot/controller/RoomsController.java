package com.naevis.bot.controller;

import java.util.ArrayList;
import java.util.List;

import com.naevis.bot.model.AppUser;
import com.naevis.bot.model.Session;
import com.naevis.bot.dto.SessionDto;
import com.naevis.bot.repository.AppUserRepository;
import com.naevis.bot.repository.SessionRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomsController {
    private final SessionRepository sessionRepository;
    private final AppUserRepository appUserRepository;

    public RoomsController(SessionRepository sessionRepository, AppUserRepository appUserRepository) {
        this.sessionRepository = sessionRepository;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/{roomCode}/sessions")
    public List<SessionDto> getSessionsByRoom(@PathVariable String roomCode) {
        List<AppUser> users = appUserRepository.findByRoomCode(roomCode);

        List<SessionDto> result = new ArrayList<>();
        for (AppUser user : users) {
            List<Session> sessions = sessionRepository.findByUserId(user.getId());

            for (Session session : sessions) {
                result.add(SessionDto.builder()
                    .id(session.getId())
                    .name(session.getName())
                    .durationMin(session.getDurationMin())
                    .telegramUser(user.getFullName() + " @" + user.getUserName())
                    .startedAt(session.getStartedAt().toInstant())
                    .build()
                );
            }
        }
        return result;
    }
}
