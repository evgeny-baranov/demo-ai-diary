package com.example.demo.service;

import com.example.demo.config.TelegramBotConfig;
import com.example.demo.domain.User;
import com.example.demo.events.TelegramPhotoMessageEvent;
import com.example.demo.events.TelegramStartMenuEvent;
import com.example.demo.events.TelegramTextMessageEvent;
import com.example.demo.events.TelegramVoiceMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot {
    TelegramBotConfig telegramBotConfig;
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    UserService userService;

    public TelegramBotService(TelegramBotConfig telegramBotConfig) {
        this.telegramBotConfig = telegramBotConfig;
        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(
                new BotCommand(
                        "/start",
                        "get a welcome message"
                )
        );
        botCommandList.add(
                new BotCommand(
                        "/taskList",
                        "Get TODO list"
                )
        );
        try {
            this.execute(
                    new SetMyCommands(
                            botCommandList,
                            new BotCommandScopeDefault(),
                            null
                    )
            );
        } catch (TelegramApiException e) {
            log.error("Error setting bot command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return telegramBotConfig.getName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            User user = userService.addOrCreateTelegramUser(
                    update.getMessage().getFrom()
            );

            // handle user photo message - not implemented
            if (update.getMessage().hasPhoto()) {
                eventPublisher.publishEvent(
                        new TelegramPhotoMessageEvent(
                                chatId,
                                user,
                                update.getMessage()
                        )
                );
            }

            if (update.getMessage().hasVoice()) {
                eventPublisher.publishEvent(
                        new TelegramVoiceMessageEvent(
                                chatId,
                                user,
                                update.getMessage()
                        )
                );
            }

            // handle text message
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();

                switch (messageText) {
                    case "/start":
                        eventPublisher.publishEvent(
                                new TelegramStartMenuEvent(
                                        chatId,
                                        user,
                                        update.getMessage()
                                )
                        );
                        break;
                    default:
                        eventPublisher.publishEvent(
                                new TelegramTextMessageEvent(
                                        chatId,
                                        user,
                                        update.getMessage()
                                )
                        );
                }
            }
        }
    }

    public void sendReplyToMessage(long chatId, String textToSend, Integer replyToMessageId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyToMessageId(replyToMessageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public String getPhotoUrl(Message message) throws TelegramApiException {
        if (message.getPhoto() == null || message.getPhoto().isEmpty()) {
            throw new TelegramApiException("No photos available in the message.");
        }

        // Find the tallest photo
        PhotoSize maxPhoto = message.getPhoto().stream()
                .max(Comparator.comparingInt(PhotoSize::getHeight))
                .orElseThrow(() -> new TelegramApiException("Failed to find the largest photo."));

        // Create a GetFile method instance with the file ID of the tallest photo
        GetFile getFile = new GetFile(maxPhoto.getFileId());

        // Execute the getFile request and return the URL
        return execute(getFile).getFileUrl(this.getBotToken());
    }

    public String getVoiceUrl(Message message) throws TelegramApiException {
        GetFile getFile = new GetFile(message.getVoice().getFileId());

        File file = execute(getFile);
        return file.getFileUrl(getBotToken());
    }
}
