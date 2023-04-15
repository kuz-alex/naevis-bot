package com.naevis.bot.command;

import java.util.Optional;

import com.naevis.bot.model.AppUser;
import com.naevis.bot.model.Room;
import com.naevis.bot.repository.AppUserRepository;
import com.naevis.bot.service.RoomService;
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
public class CreateRoomCommand extends BotCommand implements ICommand {
    public static final String COMMAND_NAME = "create_room";
    public static final String DESCRIPTION = "Создать комнату для учебы";
    public static final String USAGE = "Создаём комнату с `/create_room <name>`, после чего бот выдаст room_id," +
                                       "который отправляем участникам для подключения к комнате (`/join_room <room_id>`)";

    private final AppUserRepository appUserRepository;
    private final RoomService roomService;

    public CreateRoomCommand(AppUserRepository appUserRepository, RoomService roomService) {
        super(COMMAND_NAME, DESCRIPTION);
        this.appUserRepository = appUserRepository;
        this.roomService = roomService;
    }

    @Override
    public void processCommand(String[] args, Message message, AbsSender bot) throws TelegramApiException {
        if (args.length == 0) {
            bot.execute(this.buildMessage(message, "Название комнаты должно быть передано первым аргументом"));
            return;
        }
        String roomName = args[0];

        Long telegramUserId = message.getFrom().getId();
        Optional<AppUser> appUserOptional = appUserRepository.findByTelegramId(telegramUserId);

        if (appUserOptional.isEmpty()) {
            // TODO: Create user here.
            bot.execute(this.buildMessage(message,"Для доступа к этому функционалу выполните команду `start`"));
            return;
        }

        AppUser user = appUserOptional.get();
        Room createdRoom = roomService.createRoom(user, roomName);

        bot.execute(this.buildMessage(message, String.format("Ваш код комнаты: %s", createdRoom.getId())));
    }

    private SendMessage buildMessage(Message message, String text) {
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(text)
                .build();
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
