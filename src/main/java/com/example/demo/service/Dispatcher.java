package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.events.PhotoMessageEvent;
import com.example.demo.events.StartCommandEvent;
import com.example.demo.events.TextMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Dispatcher {

    @Value("${debug:false}")
    private boolean debugMode;

    @Autowired
    UserService userService;

    @Autowired
    TelegramBotService telegramBotService;

    @Autowired
    ChatGptService chatGptService;

    @EventListener
    void onStartMessageReceived(StartCommandEvent event) {
        User user = userService.addOrCreateTelegramUser(event.getFrom());
        telegramBotService.sendMessage(
                event.getChatId(),
                "Hi, "
                        + user.getTelegramUser().getFirstName()
                        + ", nice to meet you!"
        );
        if (debugMode) {
            telegramBotService.sendMessage(
                    event.getChatId(),
                    "User data saved: "
                            + user.toString()
            );
        }
    }

    @EventListener
    void onTextMessageReceived(TextMessageEvent event) {
        log.info("onTextMessageReceived: " + event.getMessage().getText());
        User user = userService.addOrCreateTelegramUser(event.getFrom());
        try {
            telegramBotService.sendMessage(
                    event.getChatId(),
                    chatGptService.getOpenaiResponse(
                            getSystemPrompt(user),
                            event.getMessage().getText()
                    )
            );
        } catch (Exception error) {
            if (debugMode) {
                telegramBotService.sendMessage(
                        event.getChatId(),
                        error.toString()
                );
            }
            log.debug(error.toString());
        }
    }

    private String getSystemPrompt(User user) {
        return "Make connection to user " + user.getTelegramUser().getFirstName()
                + " on language " + user.getTelegramUser().getLanguageCode();
    }

    @EventListener
    void onPhotoMessageReceived(PhotoMessageEvent event) {
        log.info("onPhotoMessageReceived: "
                + event.getMessage().getPhoto().toString()
        );
    }
}
