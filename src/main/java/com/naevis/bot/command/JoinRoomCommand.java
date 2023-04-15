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
public class JoinRoomCommand extends BotCommand implements ICommand {
    public static final String COMMAND_NAME = "join_room";
    public static final String DESCRIPTION = "Зайти в комнату для учебы";
    public static final String USAGE = "`!join_room <room_id>`, где `min` необязательный параметр кол-ва минут. Например, " +
                                       "`!join_room 23` для 60-минутной учебной сессии. По умолчанию время сессии 90 " +
                                       "минут.";

    private final SessionService sessionService;
    private final AppUserRepository appUserRepository;

    public JoinRoomCommand(AppUserRepository appUserRepository, SessionService sessionService) {
        super(COMMAND_NAME, DESCRIPTION);
        this.appUserRepository = appUserRepository;
        this.sessionService = sessionService;
    }

    @Override
    public void processCommand(String[] args, Message message, AbsSender bot) throws TelegramApiException {
        Long telegramUserId = message.getFrom().getId();
        Optional<AppUser> appUserOptional = appUserRepository.findByTelegramId(telegramUserId);

        if (appUserOptional.isEmpty()) {
            bot.execute(SendMessage.builder()
                    .chatId(message.getChatId().toString())
                    .text("Для доступа к этому функционалу выполните команду `start`")
                    .build()
            );
            return;
        }

        AppUser user = appUserOptional.get();
        Session session = sessionService.createSession(user, "testeroni");
        System.out.println("555test: " + session.getName() + ", " + session.getDurationMin());
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public @NonNull String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public String getUsage() {
        return USAGE;
    }
}
