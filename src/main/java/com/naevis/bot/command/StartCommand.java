package com.naevis.bot.command;

import com.naevis.bot.model.AppUser;
import com.naevis.bot.properties.TelegramProperties;
import com.naevis.bot.repository.AppUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
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
        User telegramUser = message.getFrom();
        Long id = telegramUser.getId();
        String userName = telegramUser.getUserName();
        String fullName = telegramUser.getFirstName() + " " + telegramUser.getLastName();

        appUserRepository.findByTelegramId(id).ifPresentOrElse(
        user -> log.info("User already exist: {}", user),
        () -> {
            AppUser user = AppUser.builder()
                    .telegramId(id)
                    .userName(userName)
                    .fullName(fullName)
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
