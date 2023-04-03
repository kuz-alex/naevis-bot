package com.naevis.bot.command;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.naevis.bot.service.YoutubeClipperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class ClipSubsCommand extends BotCommand implements ICommand {
    public static String USAGE = """
            Команда для вырезки клипа с субтитрами:
            `/clip_subs video_id start end tag1 tag2 ...`
                           
            Аргументы:
            • `video_id - ссылка или строчка после v= из ссылки на видео на YouTube (например, dQw4w9WgXcQ из ссылки https://www.youtube.com/watch?v=dQw4w9WgXcQ)
            • `start` - время начала вырезки в формате минуты:секунды.миллисекунды, например `2:39.89`
            • `end` - время конца вырезки в формате минуты:секунды.миллисекунды, например `3:29.00`
            • `tag1, tag2, ...` - необязательные теги для описания видео
            """;

    public static String COMMAND_NAME = "clip_subs";
    public static String DESCRIPTION = String.format("Команда для вырезки клипа из видео на YouTube с субтитрами (hardsub). (Использование: /help %s)", COMMAND_NAME);

    public ClipSubsCommand() {
        super(COMMAND_NAME, DESCRIPTION);
    }

    public String getCommandName() {
        return COMMAND_NAME;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public String getUsage() {
        return USAGE;
    }

    @Override
    public void processCommand(String[] args, Message message, AbsSender bot) throws TelegramApiException {
        log.info("Processing: {}", Arrays.toString(args));

        if (args.length < 3) {
            return;
        }

        String link = args[0];
        String start = args[1];
        String end = args[2];
        String[] rest = Arrays.copyOfRange(args, 3, args.length); // tags

        try {
            String fullPath = new YoutubeClipperService().clipVideo(link, start, end, true);

            bot.execute(SendVideo.builder()
                    .chatId(message.getChatId().toString())
                    .supportsStreaming(Boolean.TRUE)
                    .caption(formatTags(rest))
                    .video(new InputFile(new File(fullPath)))
                    .build());
        } catch (IOException | InterruptedException e) {
            log.error("Error processing a video: {}", e.toString());
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
