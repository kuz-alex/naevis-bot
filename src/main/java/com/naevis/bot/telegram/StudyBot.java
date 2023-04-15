package com.naevis.bot.telegram;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.naevis.bot.command.ICommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class StudyBot extends TelegramLongPollingBot {
    private final List<ICommand> commands;
    private final String botName;

    @Override
    public String getBotUsername() {
        return botName;
    }

    public StudyBot(@Value("${telegram.bot.token}") String botToken, @Value("${telegram.bot.name}") String botName,
                    List<ICommand> commands) {
        super(botToken);
        this.botName = botName;
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
            log.error("Error on bot startup: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            // TODO: Error handling for empty input.
            return;
        }

        String input = update.getMessage().getText().trim();
        String[] parts = input.split("\\s+");

        String commandName = parts[0];
        if (commandName.equals("/help") && parts[1] != null) {
            processHelpCommand(parts[1], update.getMessage());
            return;
        }

        ICommand cmd = getCommand(commandName);
        if (cmd == null) {
            return;
        }

        String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);

        try {
            cmd.processCommand(commandArgs, update.getMessage(), this);
        } catch (TelegramApiException e) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText("К сожалению, не удалось загрузить и обработать запрошенное видео. Пожалуйста, " +
                            "проверьте правильность ссылки и попробуйте еще раз.");
            try {
                execute(message);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void processHelpCommand(String commandToDisplayUsage, Message message) {
        ICommand command = getCommand(commandToDisplayUsage);
        if (command == null) {
            return;
        }

        try {
            execute(SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(command.getUsage())
                    .disableWebPagePreview(Boolean.TRUE)
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private ICommand getCommand(String commandName) {
        String commandNameWithoutSlash = commandName.startsWith("/") ? commandName.substring(1) : commandName;

        for (ICommand cmd : commands) {
            if (cmd.getCommandName().equals(commandNameWithoutSlash)) {
                return cmd;
            }
        }
        return null;
    }
}
