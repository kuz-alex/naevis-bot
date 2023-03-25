package com.naevis.bot;

import java.io.File;
import java.util.Arrays;

import com.naevis.bot.service.YoutubeClipperService;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@SpringBootApplication
@Configuration
@Getter
public class BotApplication {
	// TODO: We don't want to show preview for the message, thus only accepting: "QZHbypZSTGg"
	// final static String YT_LINK_PATTERN = "^https?://(?:www\\.|m\\.)?((?:youtube\\.com|youtu.be)).+";

	public static void main(String[] args) {
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			// Register long polling bots. They work regardless type of TelegramBotsApi we are creating
			botsApi.registerBot(new StudyTrackerBot());
		} catch (Exception e) {
			System.out.println(e);
		}

		SpringApplication.run(BotApplication.class, args);
	}

	public static class StudyTrackerBot extends TelegramLongPollingBot {
		private final String USAGE = """
				Команда для вырезки клипа:
				`/clip video_id start end tag1 tag2 ...`
				    
				Аргументы:
				• `video_id - строчка после v= из ссылки на видео на YouTube, например вот вырезано dQw4w9WgXcQ из ссылки https://www.youtube.com/watch?v=dQw4w9WgXcQ
				• `start` - время начала вырезки в формате минуты:секунды.миллисекунды, например `2:39.89`
				• `end` - время конца вырезки в формате минуты:секунды.миллисекунды, например `3:29.00`
				• `tag1, tag2, ...` - необязательные теги для описания видео
				""";

		@Override
		public String getBotUsername() {
			return "naevisRecallBot";
		}

		@Override
		public String getBotToken() {
			return "";
		}

		@Override
		public void onUpdateReceived(Update update) {
			if (!update.hasMessage() || !update.getMessage().hasText()) { return; }

			String input = update.getMessage().getText();
			String[] parts = input.split("\\s+");
			if (parts.length < 4) { return; }

			String cmd = parts[0];
			String link = parts[1];
			String start = parts[2];
			String end = parts[3];
			String[] rest = Arrays.copyOfRange(parts, 4, parts.length); // tags

			if (!cmd.matches("/clip") && !cmd.matches("/clip_subs")) { return; }

			System.out.println("list: " + cmd + " , " + link + " , " + start + " , " + end + ", " + Arrays.toString(rest));

			try {
				Boolean withSubs = cmd.matches("/clip_subs");
				String fullPath = new YoutubeClipperService().clipVideo(link, start, end, withSubs);

				SendVideo outMessage = new SendVideo();
				outMessage.setChatId(update.getMessage().getChatId().toString());
				outMessage.setCaption(formatTags(rest));
				outMessage.setVideo(new InputFile(new File(fullPath)));

				try {
					execute(outMessage); // Call method to send the message
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			} catch (Exception ex) {
				SendMessage message = new SendMessage();
				message.setChatId(update.getMessage().getChatId().toString());
				message.setText("К сожалению, не удалось загрузить и обработать запрошенное видео. Пожалуйста, " +
								"проверьте правильность ссылки и попробуйте еще раз.");
				try {
					execute(message); // Call method to send the message
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			}
		}

		public static String formatTags(String[] tags) {
			StringBuilder sb = new StringBuilder();
			for (String tag : tags) {
				if (!tag.startsWith("#")) {
					sb.append("#");
				}
				sb.append(tag).append(" ");
			}
			return sb.toString().trim();
		}
	}
}
