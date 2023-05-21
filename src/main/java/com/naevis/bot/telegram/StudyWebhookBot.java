package com.naevis.bot.telegram;

import java.util.Arrays;
import java.util.List;

import com.naevis.bot.command.AbstractBotCommand;
import com.naevis.bot.properties.TelegramProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Component
@ConditionalOnProperty(prefix = "telegram", name = "useWebhook", havingValue = "true")
@Slf4j
public class StudyWebhookBot extends SpringWebhookBot {
    private final List<AbstractBotCommand> commands;
    private final TelegramProperties properties;

    public StudyWebhookBot(TelegramProperties properties, List<AbstractBotCommand> commands) {
        super(
                SetWebhook.builder()
                        .url(properties.getWebhookUrl())
                        .build(),
                properties.getToken()
        );
        this.properties = properties;
        this.commands = commands;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        log.info("getting webhook update: {}", update);
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            // Skipping empty input.
            return null;
        }

        String input = update.getMessage().getText().trim();
        String[] parts = input.split("\\s+");

        String commandName = parts[0];
        if (commandName.equals("/help")) {
            processHelpCommand(parts, update.getMessage());
            return null;
        }

        AbstractBotCommand cmd = getCommand(commandName);
        if (cmd == null) {
            return null;
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
        return null;
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

    @Override
    public String getBotPath() {
        return properties.getWebhookPath();
    }

    @Override
    public String getBotUsername() {
        return properties.getName();
    }
}
