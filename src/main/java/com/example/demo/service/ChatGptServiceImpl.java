package com.example.demo.service;

import com.example.demo.config.OpenAiConfig;
import com.example.demo.config.TodoToolsConfig;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.events.BotTextMessageEvent;
import com.example.demo.events.BotToolCallMessageEvent;
import com.example.demo.events.BotToolResultMessageEvent;
import com.example.demo.openai.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ChatGptServiceImpl implements ChatGptService {
    @Autowired
    RecordService recordService;

    @Autowired
    TodoToolsConfig todoToolsConfig;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    OpenAiConfig openAiConfig;

    @Autowired
    OpenAiClient openAiClient;

    private String getGeneralPrompt(User user) {
        return openAiConfig.getModels().getCompletions().getPrompt().formatted(
                user.getTelegramUser().getFirstName(),
                user.getTelegramUser().getLanguageCode()
        );
    }

    public void makeUserCompletionRequest(long chatId, User user, String messageText) {
        Message message = new Message(
                Message.MessageRole.user,
                messageText
        );

        this.makeCompletionRequest(
                chatId,
                user,
                List.of(message)
        );
    }

    public TranscriptionResponse makeWhisperRequest(byte[] audioData) {
        return this.openAiClient.createTranscription(new TranscriptionRequest(
                openAiConfig.getModels().getTranscriptions().getName(),
                new CustomMultipartFile(
                        "voice-message.ogg",
                        "voice-message.ogg",
                        "audio/ogg",
                        audioData
                )
        ));
    }

    private void makeRequest(long chatId, User user, CompletionRequest request) {
        CompletionResponse response = openAiClient.createCompletion(request);

        response.getChoices()
                .stream().map(choice -> choice.isFunction()
                        ? new BotToolCallMessageEvent(chatId, user, choice.getMessage())
                        : new BotTextMessageEvent(chatId, user, choice.getMessage())
                ).forEachOrdered(
                        event -> this.eventPublisher.publishEvent(event)
                );
    }

    @Override
    public void makeCompletionRequest(long chatId, User user, List<Message> messageList) {
        CompletionRequest request = buildCompletionRequest(
                chatId,
                user
        );

        request.getMessages().addAll(messageList);

        messageList.forEach(message -> eventPublisher.publishEvent(
                new BotToolResultMessageEvent(chatId, user, message)
        ));

        makeRequest(chatId, user, request);
    }

    @Override
    public void makeCompletionRequest(long chatId, User user, Message message) {
        this.makeCompletionRequest(
                chatId,
                user,
                List.of(message)
        );
    }

    private CompletionRequest buildCompletionRequest(long chatId, User user) {
        CompletionRequest request = new CompletionRequest(
                this.openAiConfig.getModels().getCompletions().getName()
        );

        // add system prompt
        request.addMessage(
                new Message(
                        Message.MessageRole.system,
                        this.getGeneralPrompt(user)
                )
        );

        // add message history
        recordService.getHistoryRecords(chatId).stream()
                .map(Record::getMessage)
                .forEachOrdered(request::addMessage);

        // add tools section
        request.addTool(todoToolsConfig.buildAddTool());

        request.addTool(todoToolsConfig.buildDeleteTool());

        request.addTool(todoToolsConfig.buildListTool());

        return request;
    }
}
