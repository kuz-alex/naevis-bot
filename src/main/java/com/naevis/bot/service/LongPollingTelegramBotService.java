package com.naevis.bot.service;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Log4j2
@Getter
public class LongPollingTelegramBotService implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(LongPollingTelegramBotService.class);

    @Value("${TELEGRAM_TOKEN}")
    private String botToken;

    @Value("${telegram.bot.name}")
    private String botUsername;

    private final ExecutorService botExecutor;
    private final TelegramLongPollingBot client;

    public LongPollingTelegramBotService(
            TelegramBotsApi api, ConfigurableBeanFactory configurableBeanFactory
    ) {
        logger.info("Registering Long Polling {} and {}", botUsername, botToken);
        botExecutor = Executors.newSingleThreadExecutor();

        client = new TelegramClient();
        try {
            api.registerBot(client);
        } catch (TelegramApiException e) {
            logger.error("Can not register Long Polling with {} and {}", botUsername, botToken, e);
            throw new RuntimeException(e);
        }
    }

    public TelegramLongPollingBot getClient() {
        return client;
    }

    @Override
    public void close() {
        botExecutor.shutdownNow();
    }

    private class TelegramClient extends TelegramLongPollingBot {
        @Override
        public void onUpdateReceived(Update update) {
                botExecutor.execute(() -> {
                    updateProcess(update).ifPresent(result -> {
                        try {
                            getClient().execute(result);
                            logger.debug("Update: {}. Message: {}. Successfully sent", update, result);
                        } catch (TelegramApiException e) {
                            logger.error("Update: {}. Can not send message {} to telegram: ", update, result, e);
                        }
                    });
                });
        }

        @Override
        public String getBotUsername() {
            return botUsername;
        }
    }

    public Optional<BotApiMethod<?>> updateProcess(Update update) {
        logger.debug("Update {} received", update);
        return Optional.empty();
    }
}
