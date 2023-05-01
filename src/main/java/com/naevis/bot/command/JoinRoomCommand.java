package com.naevis.bot.command;

import java.util.Optional;

import com.naevis.bot.model.AppUser;
import com.naevis.bot.model.Room;
import com.naevis.bot.properties.TelegramProperties;
import com.naevis.bot.repository.AppUserRepository;
import com.naevis.bot.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class JoinRoomCommand extends AbstractBotCommand {
    public static final String USAGE = "`!join_room <room_id>`, где `min` необязательный параметр кол-ва минут. " +
                                       "Например, " +
                                       "`!join_room 23` для 60-минутной учебной сессии. По умолчанию время сессии 90 " +
                                       "минут.";

    private final AppUserRepository appUserRepository;
    private final RoomRepository roomRepository;

    public JoinRoomCommand(AppUserRepository appUserRepository,
                           RoomRepository roomRepository,
                           TelegramProperties properties) {
        super("join_room", properties.getCommand().get("join_room"), USAGE, 1);
        this.appUserRepository = appUserRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public void processCommandImpl(String[] args, Message message, AbsSender bot) throws TelegramApiException {
        String roomId = args[0];
        Optional<Room> roomOptional = roomRepository.findByIdWithJoinedUsers(Long.valueOf(roomId));

        if (roomOptional.isEmpty()) {
            bot.execute(this.buildMessage(message, String.format(
                    "Не удалось найти комнату по коду %s", roomId)));
            return;
        }

        Long telegramUserId = message.getFrom().getId();
        Optional<AppUser> appUserOptional = appUserRepository.findByTelegramId(telegramUserId);

        if (appUserOptional.isEmpty()) {
            bot.execute(this.buildMessage(message, "Для доступа к этому функционалу выполните команду `join_room`"));
            return;
        }

        Room room = roomOptional.get();
        room.getJoinedUsers().add(appUserOptional.get());
        roomRepository.save(room);
    }

    private SendMessage buildMessage(Message message, String text) {
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(text)
                .build();
    }
}
