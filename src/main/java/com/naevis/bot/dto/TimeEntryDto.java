package com.naevis.bot.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimeEntryDto {
    private Long id;
    private String name;
    private Integer durationMin;
    private Instant startedAt;
    private Instant stoppedAt;
    private String telegramUser;
}
