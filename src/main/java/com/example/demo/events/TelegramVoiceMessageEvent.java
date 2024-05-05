package com.example.demo.events;

import com.example.demo.domain.User;
import org.telegram.telegrambots.meta.api.objects.Message;

public class TelegramVoiceMessageEvent extends TelegramMessageEvent {
    public TelegramVoiceMessageEvent(long chatId, User user, Message source) {
        super(chatId, user, source);
    }
}
