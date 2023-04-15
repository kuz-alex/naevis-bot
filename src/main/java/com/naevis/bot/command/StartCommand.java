package com.naevis.bot.command;

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
public class StartCommand extends BotCommand implements ICommand {
    public static final String COMMAND_NAME = "start";
    public static final String DESCRIPTION = "Bot initialization";
    public static final String USAGE = "";

    private final AppUserRepository appUserRepository;

    public StartCommand(AppUserRepository appUserRepository) {
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
                .text("Вы можете использовать все возможности бота.")
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
