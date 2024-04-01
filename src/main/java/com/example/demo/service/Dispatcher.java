package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.events.StartCommandEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@Slf4j
public class Dispatcher {

    @Value("${debug:false}")
    private boolean debugMode;

    @Autowired
    UserService userService;

    @Autowired
    TelegramBotService telegramBotService;

    @EventListener({StartCommandEvent.class})
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

}
