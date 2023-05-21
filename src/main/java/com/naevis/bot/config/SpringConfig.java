package com.naevis.bot.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.updatesreceivers.ServerlessWebhook;

@Configuration
public class SpringConfig {
    @Bean
    @ConditionalOnProperty(prefix = "telegram", name = "useWebhook", havingValue = "false")
    public TelegramBotsApi telegramBotsApiLongPolling() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    @ConditionalOnProperty(prefix = "telegram", name = "useWebhook", havingValue = "true")
    public TelegramBotsApi telegramBotsApiWebhook() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class, new ServerlessWebhook());
    }
}
