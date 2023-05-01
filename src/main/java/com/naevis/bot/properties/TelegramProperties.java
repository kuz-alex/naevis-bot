package com.naevis.bot.properties;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "telegram")
@Component
public class TelegramProperties {
    private String name;
    private String token;
    private Map<String, String> command;
    private Map<String, String> message;
}
