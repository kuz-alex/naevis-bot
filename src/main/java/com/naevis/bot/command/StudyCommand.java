package com.naevis.bot.command;

import java.util.Optional;

import com.naevis.bot.model.AppUser;
import com.naevis.bot.model.Session;
import com.naevis.bot.repository.AppUserRepository;
import com.naevis.bot.service.SessionService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class StudyCommand extends BotCommand implements ICommand {
    public static final String COMMAND_NAME = "study";
    public static final String DESCRIPTION = "Начать учебную сессию";
    public static final String USAGE = "`!study <min>`, где `min` необязательный параметр кол-ва минут. Например, " +
                                       "`!study 60` для 60-минутной учебной сессии. По умолчанию время сессии 90 " +
                                       "минут.";

    private final SessionService sessionService;
    private final AppUserRepository appUserRepository;

    public StudyCommand(AppUserRepository appUserRepository, SessionService sessionService) {
        super(COMMAND_NAME, DESCRIPTION);
        this.appUserRepository = appUserRepository;
        this.sessionService = sessionService;
    }

    @Override
    public void processCommand(String[] args, Message message, AbsSender bot) throws TelegramApiException {
        if (args.length == 0) {
            bot.execute(this.buildMessage(message, "Название сессии должно идти первым аргументом"));
            return;
        }

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

    public String getCommandName() {
        return COMMAND_NAME;
    }

    public @NonNull String getDescription() {
        return DESCRIPTION;
    }

    public String getUsage() {
        return USAGE;
    }
}
