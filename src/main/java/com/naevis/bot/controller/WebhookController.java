package com.naevis.bot.controller;

import com.naevis.bot.telegram.StudyWebhookBot;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("/callback")
@ConditionalOnProperty(prefix = "telegram", name = "useWebhook", havingValue = "true")
public class WebhookController {
    private final StudyWebhookBot studyWebhookBot;

    public WebhookController(StudyWebhookBot studyWebhookBot) {
        this.studyWebhookBot = studyWebhookBot;
    }

    @PostMapping("/naevis")
    public BotApiMethod<?> webhookNaevis(@RequestBody Update update) {
        return studyWebhookBot.onWebhookUpdateReceived(update);
    }
}
