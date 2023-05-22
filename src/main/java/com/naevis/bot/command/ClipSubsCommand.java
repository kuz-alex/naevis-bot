package com.naevis.bot.command;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.naevis.bot.properties.TelegramProperties;
import com.naevis.bot.service.YoutubeClipperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class ClipSubsCommand extends AbstractBotCommand {
    public static final String USAGE = String.format("""
            Команда для вырезки клипа с субтитрами:
            `clip_subs video_id start end tag1 tag2 ...`
                           
            Аргументы:
            • `video_id` - ссылка или строчка после v= из ссылки на видео на YouTube (например, dQw4w9WgXcQ из ссылки https://www.youtube.com/watch?v=dQw4w9WgXcQ)
            • `start` - время начала вырезки в формате минуты:секунды.миллисекунды, например `2:39.89`
            • `end` - время конца вырезки в формате минуты:секунды.миллисекунды, например `3:29.00`
            • `tag1, tag2, ...` - необязательные теги для описания видео
            """);

    public ClipSubsCommand(TelegramProperties properties) {
        super("clip_subs", properties.getCommand().get("clip_subs"), USAGE, 3);
    }

    @Override
    public void processCommandImpl(String[] args, Message message, AbsSender sender) {
        String link = args[0];
        String start = args[1];
        String end = args[2];
        String[] rest = Arrays.copyOfRange(args, 3, args.length); // tags

        try {
            YoutubeClipperService.VideoInfo video = YoutubeClipperService.clipVideo(link, start, end, true, false);

            SendVideo answer = SendVideo.builder()
                    .chatId(message.getChatId().toString())
                    .supportsStreaming(Boolean.TRUE)
                    .width(video.getWidth())
                    .height(video.getHeight())
                    .caption(formatTags(rest))
                    .thumb(new InputFile(new File(video.getThumbnailPath())))
                    .video(new InputFile(new File(video.getPath())))
                    .build();
            sender.execute(answer);
        } catch (IOException | InterruptedException | TelegramApiException e) {
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
