package com.naevis.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.naevis.bot.telegram.StudyBot;

@SpringBootApplication
public class BotApplication {
	// TODO: We don't want to show preview for the message, thus only accepting: "QZHbypZSTGg"
	// final static String YT_LINK_PATTERN = "^https?://(?:www\\.|m\\.)?((?:youtube\\.com|youtu.be)).+";

	public BotApplication(StudyBot bot) {
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(bot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(BotApplication.class, args);
	}
}
