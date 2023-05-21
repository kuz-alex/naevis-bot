package com.naevis.bot.telegram;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.naevis.bot.command.AbstractBotCommand;
import com.naevis.bot.properties.TelegramProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@ConditionalOnProperty(prefix = "telegram", name = "useWebhook", havingValue = "false")
@Slf4j
public class StudyLongPollingBot extends TelegramLongPollingBot {
    private final List<AbstractBotCommand> commands;
    private final TelegramProperties properties;

    @Override
    public String getBotUsername() {
        return properties.getName();
    }

    public StudyLongPollingBot(TelegramProperties properties, List<AbstractBotCommand> commands) {
        super(properties.getToken());
        this.properties = properties;
        this.commands = commands;

        try {
            List<BotCommand> botCommands = commands.stream()
                    .map(cmd -> new BotCommand("/" + cmd.getCommandName(), cmd.getDescription()))
                    .collect(Collectors.toList());

            botCommands.add(new BotCommand("/help", "Команда \"help\" выводит информацию об использовании других " +
                                                    "команд. Например, для получения помощи по команде \"clip\" " +
                                                    "нужно написать \"/help clip\"."));

            execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Couldn't update bot commands through SetMyCommands: {}", e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            // Skipping empty input.
            return;
        }

        String input = update.getMessage().getText().trim();
        String[] parts = input.split("\\s+");

        String commandName = parts[0];
        if (commandName.equals("/help")) {
            processHelpCommand(parts, update.getMessage());
            return;
        }

        AbstractBotCommand cmd = getCommand(commandName);
        if (cmd == null) {
            return;
        }

        String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);

        try {
            cmd.processCommand(commandArgs, update.getMessage(), this);
        } catch (TelegramApiException e) {
            SendMessage message = SendMessage.builder()
                    .chatId(update.getMessage().getChatId().toString())
                    .text("К сожалению, не удалось загрузить и обработать запрошенное видео. Пожалуйста, " +
                          "проверьте правильность ссылки и попробуйте еще раз.")
                    .build();
            try {
                execute(message);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    @SneakyThrows
    private void processHelpCommand(String[] parts, Message message) {
        if (parts.length < 2) {
            execute(SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(properties.getMessage().get("help"))
                    .disableWebPagePreview(Boolean.TRUE)
                    .build());
            return;
        }

        String commandToDisplayUsage = parts[1];
        AbstractBotCommand commandForUsage = getCommand(commandToDisplayUsage);
        if (commandForUsage == null || commandForUsage.getUsage() == null) {
            return;
        }

        execute(SendMessage.builder()
                .chatId(message.getChatId())
                .text(commandForUsage.getUsage())
                .disableWebPagePreview(Boolean.TRUE)
                .build());
    }

    private AbstractBotCommand getCommand(String commandName) {
        String commandNameWithoutSlash = commandName.startsWith("/") ? commandName.substring(1) : commandName;

        for (AbstractBotCommand cmd : commands) {
            if (cmd.getCommandName().equals(commandNameWithoutSlash)) {
                return cmd;
            }
        }
        return null;
    }
}
