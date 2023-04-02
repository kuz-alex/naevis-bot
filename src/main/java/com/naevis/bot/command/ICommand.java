package com.naevis.bot.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public interface ICommand {
    SendVideo processCommand (String[] args, Message message);
    String getCommandName();
    String getDescription();
}
