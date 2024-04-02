package com.example.demo.events;

import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

abstract public class MessageContainerEvent extends ApplicationEvent {
    public MessageContainerEvent(Object source) {
        super(source);
    }

    public Message getMessage() {
        return (Message) getSource();
    }

    public User getFrom() {
        return getMessage().getFrom();
    }

    public long getChatId() {
        return getMessage().getChatId();
    }
}
