package com.naevis.bot.service;

import com.naevis.bot.model.AppUser;
import com.naevis.bot.model.TimeEntry;
import com.naevis.bot.repository.TimeEntryRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TimeEntryService {
    private final TimeEntryRepository timeEntryRepository;

    public TimeEntryService(TimeEntryRepository timeEntryRepository) {
        this.timeEntryRepository = timeEntryRepository;
    }

    public TimeEntry create(AppUser user, String name, Integer duration) {
        return timeEntryRepository.save(TimeEntry.builder()
                .name(name)
                .user(user)
                .durationMin(duration)
                .build()
        );
    }

    public TimeEntry update(Long timeEntryId, Instant stoppedAt) {
        // TODO: Figure out if we're setting stoppedAt without timezone.
        TimeEntry timeEntry = timeEntryRepository.findById(timeEntryId)
                .orElseThrow(() -> new RuntimeException("Time entry not found"));

        timeEntry.setStoppedAt(stoppedAt);
        return timeEntryRepository.save(timeEntry);
    }
}
