package com.naevis.bot.telegram;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.naevis.bot.command.ClipCommand;
import com.naevis.bot.command.ClipSubsCommand;
import com.naevis.bot.command.ICommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
@Slf4j
public class StudyBot extends TelegramLongPollingBot {
    private final List<ICommand> iCommands;

    @Override
    public String getBotUsername() {
        return "naevisRecallBot";
    }

    @Autowired
    public StudyBot(@Value("${telegram.bot.token}") String botToken, ClipCommand clipCommand, ClipSubsCommand clipSubsCommand) {
        super(botToken);

        iCommands = Arrays.asList(clipCommand, clipSubsCommand);

        try {
            List<BotCommand> botCommands = iCommands.stream()
                    .map(cmd -> new BotCommand(cmd.getCommandName(), cmd.getDescription()))
                    .collect(Collectors.toList());

            execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), "en"));
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
        ICommand cmd = getCommand(commandName);
        if (cmd == null) {
            return;
        }

        String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);

        try {
            execute(cmd.processCommand(commandArgs, update.getMessage()));
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

    private ICommand getCommand(String commandName) {
        for (ICommand cmd : iCommands) {
            if (cmd.getCommandName().equals(commandName)) {
                return cmd;
            }
        }
        return null;
    }
}
