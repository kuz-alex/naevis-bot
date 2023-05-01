package com.naevis.bot.command;

import com.naevis.bot.model.AppUser;
import com.naevis.bot.properties.TelegramProperties;
import com.naevis.bot.repository.AppUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class StartCommand extends AbstractBotCommand {
    private final AppUserRepository appUserRepository;

    public StartCommand(AppUserRepository appUserRepository, TelegramProperties properties) {
        super("start", properties.getCommand().get("start"));
        this.appUserRepository = appUserRepository;
    }

    @Override
    public void processCommandImpl(String[] args, Message message, AbsSender bot) throws TelegramApiException {
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
}
