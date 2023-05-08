package com.naevis.bot.service;

import com.naevis.bot.model.AppUser;
import com.naevis.bot.model.Session;
import com.naevis.bot.repository.SessionRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session createSession(AppUser user, String name, Integer duration) {
        return sessionRepository.save(Session.builder()
                .name(name)
                .user(user)
                .durationMin(duration)
                .build()
        );
    }
}
