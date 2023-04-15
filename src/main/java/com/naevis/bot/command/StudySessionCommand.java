package com.naevis.bot.command;

import com.naevis.bot.command.ICommand;
import com.naevis.bot.model.AppUser;
import com.naevis.bot.repository.AppUserRepository;
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
public class StudySessionCommand extends BotCommand implements ICommand {
    public static final String COMMAND_NAME = "study";
    public static final String DESCRIPTION = "Начать учебную сессию";
    public static final String USAGE = "`!study <min>`, где `min` необязательный параметр кол-ва минут. Например, " +
                                       "`!study 60` для 60-минутной учебной сессии. По умолчанию время сессии 90 " +
                                       "минут.";

    private final AppUserRepository appUserRepository;

    public StudySessionCommand(AppUserRepository appUserRepository) {
        super(COMMAND_NAME, DESCRIPTION);
        this.appUserRepository = appUserRepository;
    }

    @Override
    public void processCommand(String[] args, Message message, AbsSender bot) throws TelegramApiException {
        Long telegramUserId = message.getFrom().getId();
        String userName = message.getFrom().getUserName();

        appUserRepository.findByTelegramId(telegramUserId).ifPresentOrElse(
                user -> log.info("User already exist: {}", user),
                () -> {
                    AppUser user = AppUser.builder()
                            .telegramId(telegramUserId)
                            .userName(userName)
                            .build();
                    log.info("Creating the user: {}", user);

                    appUserRepository.save(user);
                });

        SendMessage result = SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Теперь вы можете использовать все возможности бота.")
                .build();
        bot.execute(result);
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
0
