package com.naevis.bot.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomDto {
    private Long id;
    private String name;
    private String code;
    private Instant createdAt;
}
