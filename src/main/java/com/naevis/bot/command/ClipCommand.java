package com.naevis.bot.command;

import java.io.File;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import com.naevis.bot.service.YoutubeClipperService;

@Component
@Slf4j
public class ClipCommand extends BotCommand implements ICommand {
    public static String USAGE = """
            Команда для вырезки клипа:
            `/clip video_id start end tag1 tag2 ...`
                           
            Аргументы:
            • `video_id - ссылка или строчка после v= из ссылки на видео на YouTube (например, dQw4w9WgXcQ из ссылки https://www.youtube.com/watch?v=dQw4w9WgXcQ)
            • `start` - время начала вырезки в формате минуты:секунды.миллисекунды, например `2:39.89`
            • `end` - время конца вырезки в формате минуты:секунды.миллисекунды, например `3:29.00`
            • `tag1, tag2, ...` - необязательные теги для описания видео
            """;

    public static String commandName = "/clip";
    public static String description = String.format("Команда для вырезки клипа из видео на YouTube. (Использование: /help %s)", commandName);

    public ClipCommand() {
        super(commandName, description);
    }

    public String getCommandName() {
        return commandName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public SendVideo processCommand(String[] args, Message message) {
        log.info("Processing: {}", Arrays.toString(args));

        if (args.length < 4) { return null; }

        String link = args[0];
        String start = args[1];
        String end = args[2];
        String[] rest = Arrays.copyOfRange(args, 3, args.length); // tags

        try {
            String fullPath = new YoutubeClipperService().clipVideo(link, start, end, false);

            return SendVideo.builder()
                    .chatId(message.getChatId().toString())
                    .supportsStreaming(Boolean.TRUE)
                    .caption(formatTags(rest))
                    .video(new InputFile(new File(fullPath)))
                    .build();
        } catch (Exception e) {
            log.error("Error processing a video: {}", e.toString());
            throw new RuntimeException(e);
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
