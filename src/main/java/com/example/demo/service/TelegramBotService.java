package com.example.demo.service;

import com.example.demo.config.BotConfig;
import com.example.demo.domain.User;
import com.example.demo.events.StartCommandEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    final BotConfig config;

    public TelegramBotService(BotConfig config) {
        this.config = config;
        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(new BotCommand("/start", "get a welcome message"));
//        botCommandList.add(new BotCommand("/mydata", "get your data stored"));
//        botCommandList.add(new BotCommand("/deletedata", "delete my data"));
//        botCommandList.add(new BotCommand("/help", "info how to use this bot"));
//        botCommandList.add(new BotCommand("/settings", "set your preferences"));
        try {
            this.execute(
                    new SetMyCommands(
                            botCommandList,
                            new BotCommandScopeDefault(),
                            null
                    )
            );
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            log.info(update.getMessage().toString());
            log.info(update.getMessage().getFrom().toString());

            switch (messageText) {
                case "/start":
                    eventPublisher.publishEvent(
                            new StartCommandEvent(update.getMessage())
                    );
                    startCommandReceived(update.getMessage());
                    break;
                default:
                    sendMessage(chatId, "Sorry, command was not recognised");
            }

        }
    }

    private void startCommandReceived(Message message) {

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
}
