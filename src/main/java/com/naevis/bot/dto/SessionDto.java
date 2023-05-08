package com.naevis.bot.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionDto {
    private Long id;
    private String name;
    private Integer durationMin;
    private Instant startedAt;
    private String telegramUser;
}
