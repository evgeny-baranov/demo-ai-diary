package com.example.demo.events;

import org.telegram.telegrambots.meta.api.objects.Message;

public class PhotoMessageEvent extends MessageContainerEvent {
    public PhotoMessageEvent(Message message) {
        super(message);
    }
}
