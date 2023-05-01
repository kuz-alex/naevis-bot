package com.naevis.bot.command;

import java.util.Optional;

import com.naevis.bot.model.AppUser;
import com.naevis.bot.model.Session;
import com.naevis.bot.properties.TelegramProperties;
import com.naevis.bot.repository.AppUserRepository;
import com.naevis.bot.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class StudyCommand extends AbstractBotCommand {
    public static final String USAGE = "`!study <min>`, где `min` необязательный параметр кол-ва минут. Например, " +
                                       "`!study 60` для 60-минутной учебной сессии. По умолчанию время сессии 90 " +
                                       "минут.";

    private final SessionService sessionService;
    private final AppUserRepository appUserRepository;

    public StudyCommand(AppUserRepository appUserRepository, SessionService sessionService, TelegramProperties properties) {
        super("study", properties.getCommand().get("study"), USAGE, 1);
        this.appUserRepository = appUserRepository;
        this.sessionService = sessionService;
    }

    @Override
    public void processCommandImpl(String[] args, Message message, AbsSender bot) throws TelegramApiException {
        Long telegramUserId = message.getFrom().getId();
        Optional<AppUser> appUserOptional = appUserRepository.findByTelegramId(telegramUserId);

        if (appUserOptional.isEmpty()) {
            bot.execute(this.buildMessage(message, "Для доступа к этому функционалу выполните команду `start`"));
            return;
        }

        Integer duration = args.length > 1 ? Integer.valueOf(args[1]) : null;

        AppUser user = appUserOptional.get();
        Session session = sessionService.createSession(user, args[0], duration);
        bot.execute(this.buildMessage(message, String.format(
                "Сессия \"%s\" запущена на %s минут. Фокус!", session.getName(), session.getDurationMin())));

    }

    private SendMessage buildMessage(Message message, String text) {
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(text)
                .build();
    }
}
