package com.naevis.bot;

import java.io.File;

import com.naevis.bot.properties.TelegramProperties;
import com.naevis.bot.telegram.StudyLongPollingBot;
import com.naevis.bot.telegram.StudyWebhookBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class BotApplication {
    public BotApplication(StudyLongPollingBot longPollingBot,
                          StudyWebhookBot webhookBot,
                          TelegramProperties properties) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            if (properties.isUseWebhook()) {
                SetWebhook setWebhook = SetWebhook.builder()
                        .certificate(new InputFile(new File(properties.getSslCertPath())))
                        .url(properties.getWebhookUrl())
                        .build();
                botsApi.registerBot(webhookBot, setWebhook);
            } else {
                botsApi.registerBot(longPollingBot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
