package com.naevis.bot.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Service
public interface ICommand {
    void processCommand (String[] args, Message message, AbsSender bot) throws TelegramApiException;
    String getCommandName();
    String getDescription();
    String getUsage();
}
