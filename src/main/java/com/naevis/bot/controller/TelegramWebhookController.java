package com.naevis.bot.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.stereotype.Service;

import com.naevis.bot.service.WebhookTelegramBotService;

@Service
@AllArgsConstructor
@Log4j2
public class TelegramWebhookController {

    private final WebhookTelegramBotService telegramWebhookBotService;

    @PostMapping("/webhook")
    public BotApiMethod<?> webhookMapping(@RequestBody Update update) {
        return telegramWebhookBotService.onWebhookUpdateReceived(update);
    }
}
