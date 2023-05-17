package com.naevis.bot.command;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public abstract class AbstractBotCommand extends BotCommand {
    private final String name;
    private String usage;
    private int paramLength = -1;

    public AbstractBotCommand(String name, String description) {
        super(name, description);
        this.name = name;
    }

    public AbstractBotCommand(String name, String description, String usage) {
        this(name, description);
        this.usage = usage;
    }

    public AbstractBotCommand(String name, String description, String usage, int paramLength) {
        this(name, description);
        this.usage = usage;
        this.paramLength = paramLength;
    }

    public String getCommandName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public void processCommand(String[] args, Message message, AbsSender sender) throws TelegramApiException {
        log.info("Processing: {} with {}", getCommandName(), Arrays.toString(args));

        if (paramLength != -1 && args.length < paramLength) {
            sender.execute(SendMessage.builder()
                    .chatId(message.getChatId().toString())
                    .text(String.format("Не переданы обязательные аргументы (смотри /help %s)", getCommandName()))
                    .build());
            return;
        }

        processCommandImpl(args, message, sender);
    };

    protected abstract void processCommandImpl(String[] args, Message message, AbsSender sender) throws TelegramApiException;
}
