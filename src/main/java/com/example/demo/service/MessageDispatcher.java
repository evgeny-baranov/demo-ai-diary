package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.events.TelegramPhotoMessageEvent;
import com.example.demo.events.TelegramStartMenuEvent;
import com.example.demo.events.TelegramTextMessageEvent;
import com.example.demo.openai.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@PropertySource("application.properties")
public class MessageDispatcher {
    @Autowired
    TelegramBotService telegramBotService;
    @Autowired
    ChatGptService chatGptService;
    @Value("${bot.greetings}")
    private String greetings;

    @EventListener
    void onStartMessageReceived(TelegramStartMenuEvent event) {
        User user = event.getUser();
        telegramBotService.sendMessage(
                event.getChatId(),
                greetings.formatted(
                        user.getTelegramUser().getFirstName()
                )
        );
    }

    @EventListener
    void onTextMessageReceived(TelegramTextMessageEvent event) {
        try {
            chatGptService.makeUserCompletionRequest(
                    event.getChatId(),
                    event.getUser(),
                    event.getTelegramMessage().getText()
            );
        } catch (Exception error) {
            log.error(error.getMessage());
        }
    }

    @EventListener
    void onPhotoMessageReceived(TelegramPhotoMessageEvent event) {

        try {
            List<Message.ContentItem> content = new ArrayList<>();

            // add text if it exists
            String messageText = event.getTelegramMessage().getText();
            if (messageText != null && !messageText.isEmpty()) {
                content.add(
                        new Message.ContentItem(messageText)
                );
            }
            // add image url
            content.add(new Message.ContentItem(
                    new Message.ContentItem.ImageUrl(
                            telegramBotService.getFileUrl(event.getTelegramMessage()
                            )
                    )
            ));

            chatGptService.makeCompletionRequest(
                    event.getChatId(),
                    event.getUser(),
                    new Message(content
                    )
            );
        } catch (Exception error) {
            log.error("Error handling photo message: " + error.getMessage(), error);
        }
    }
}
