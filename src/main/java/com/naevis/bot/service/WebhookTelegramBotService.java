package com.naevis.bot.service;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Log4j2
@Getter
public class WebhookTelegramBotService {
    private static final String UNKNOWN_ERROR_MESSAGE = "Oops! Something went wrong and we couldn't complete your " +
                                                        "request. Please try again later or contact support if the " +
                                                        "problem persists.";

    @Value("${TELEGRAM_TOKEN}")
    private String botToken;

    @Value("${telegram.bot.name}")
    private String botName;

    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            log.trace(update.toString());
            if (update.getMessage() != null) {
                return processNewMessage(update.getMessage());
            }
            // catch (KnownException e) {
            // log.error("Caught KnownException: {}", e.getMessage());
            // return new SendMessage(update.getMessage().getChat().getId().toString(), e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            return new SendMessage(update.getMessage().getChat().getId().toString(), UNKNOWN_ERROR_MESSAGE);
        }
        log.trace("Returning `null` from `onWebhookUpdateReceived`");
        return null;
    }

    SendMessage processNewMessage(Message userMessage) {
        return new SendMessage(
                userMessage.getChat().getId().toString(),
                "echoing: " + userMessage.getText()
        );
    }
}
