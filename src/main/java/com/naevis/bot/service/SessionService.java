package com.naevis.bot.service;

import java.util.Optional;

import com.naevis.bot.model.AppUser;
import com.naevis.bot.model.Session;
import com.naevis.bot.repository.AppUserRepository;
import com.naevis.bot.repository.SessionRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session createSession(AppUser user, String name) {
        return sessionRepository.save(Session.builder()
                .name(name)
                .user(user)
                .durationMin(90)
                .build()
        );
    }
}
