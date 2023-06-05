package com.naevis.bot.controller;

import com.naevis.bot.dto.TimeEntryDto;
import com.naevis.bot.model.AppUser;
import com.naevis.bot.model.TimeEntry;
import com.naevis.bot.repository.AppUserRepository;
import com.naevis.bot.service.TimeEntryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/time-entries")
public class TimeEntriesController {
    private final AppUserRepository appUserRepository;
    private final TimeEntryService timeEntryService;

    @PostMapping()
    public ResponseEntity<TimeEntryDto> create(@RequestBody StartSessionRequest request) {
        AppUser user = appUserRepository.findByTelegramId(Long.valueOf(request.getUserTelegramId())).orElseThrow();

        TimeEntry session = timeEntryService.create(user, request.getSessionName(), request.getDuration());

        TimeEntryDto timeEntryDto = TimeEntryDto.builder()
                .id(session.getId())
                .name(session.getName())
                .durationMin(session.getDurationMin())
                .telegramUser(user.getFullName() + " @" + user.getUserName())
                .startedAt(session.getStartedAt())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(timeEntryDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeEntryDto> update(@RequestBody TimeEntryDto dto, @PathVariable("id") Long id) {
        TimeEntry timeEntry = timeEntryService.update(id, dto.getStoppedAt());
        TimeEntryDto timeEntryDto = TimeEntryDto.builder()
                .id(timeEntry.getId())
                .name(timeEntry.getName())
                .durationMin(timeEntry.getDurationMin())
                .startedAt(timeEntry.getStartedAt())
                .stoppedAt(timeEntry.getStoppedAt())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(timeEntryDto);
    }

    @Data
    @RequiredArgsConstructor
    private static class StartSessionRequest {
        private final String userTelegramId;
        private final String sessionName;
        private final int duration;
    }
}
