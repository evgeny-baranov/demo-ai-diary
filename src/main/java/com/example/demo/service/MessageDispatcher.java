package com.example.demo.service;

import com.example.demo.config.TelegramBotConfig;
import com.example.demo.domain.User;
import com.example.demo.events.TelegramPhotoMessageEvent;
import com.example.demo.events.TelegramStartMenuEvent;
import com.example.demo.events.TelegramTextMessageEvent;
import com.example.demo.events.TelegramVoiceMessageEvent;
import com.example.demo.openai.Message;
import com.example.demo.openai.TranscriptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MessageDispatcher {
    @Autowired
    TelegramBotService telegramBotService;
    @Autowired
    ChatGptService chatGptService;
    @Autowired
    TelegramBotConfig telegramBotConfig;

    @EventListener
    void onStartMessageReceived(TelegramStartMenuEvent event) {
        User user = event.getUser();
        telegramBotService.sendMessage(
                event.getChatId(),
                telegramBotConfig.getGreetings().formatted(
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
            log.error("Error handling text message", error);
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
                            telegramBotService.getPhotoUrl(event.getTelegramMessage())
                    )
            ));

            chatGptService.makeCompletionRequest(
                    event.getChatId(),
                    event.getUser(),
                    new Message(content)
            );
        } catch (Exception error) {
            log.error("Error handling photo message", error);
        }
    }

    @EventListener
    void onVoiceMessageReceived(TelegramVoiceMessageEvent event) throws MalformedURLException, TelegramApiException {
        URL url = new URL(
                telegramBotService.getVoiceUrl(
                        event.getTelegramMessage()
                )
        );

        try {
            // Step 3: Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Step 4: Check if the response was successful
            if (connection.getResponseCode() == 200) {
                // Step 5: Retrieve binary data from the response stream
                try (InputStream inputStream = connection.getInputStream()) {
                    // Step 6: Convert the InputStream into a byte array
                    byte[] audioData = inputStream.readAllBytes();

                    // Step 7: Use this binary data to make a Whisper request
                    TranscriptionResponse r = this.chatGptService.makeWhisperRequest(audioData);

                    telegramBotService.sendReplyToMessage(
                            event.getChatId(),
                            "Text from speech: " + r.getText(),
                            event.getTelegramMessage().getMessageId()
                    );

                    chatGptService.makeUserCompletionRequest(
                            event.getChatId(),
                            event.getUser(),
                            r.getText()
                    );
                }
            } else {
                throw new IOException(
                        "Failed to fetch voice data. HTTP Status Code: %d".formatted(
                                connection.getResponseCode()
                        )
                );
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}
