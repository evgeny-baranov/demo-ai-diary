package com.example.demo.service;

import com.example.demo.domain.Task;
import com.example.demo.domain.User;
import com.example.demo.events.BotReplyEvent;
import com.example.demo.message.openai.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class CommandDispatcher {
    @Autowired
    TelegramBotService telegramBotService;
    @Autowired
    JsonUtilService jsonUtilService;
    @Autowired
    private TaskService taskService;

    @EventListener
    public void onBotCommandEvent(BotReplyEvent event) {
        if (event.getDto().choice().isFunction()) {
            event.getDto().choice().getMessage().getTool_calls().stream()
                    .map(
                            toolCall -> {
                                try {
                                    return switch (toolCall.getFunction().getName()) {
                                        case "todo_add" -> handleTodoAddFunction(
                                                event.getDto().chatId(),
                                                event.getDto().user(),
                                                toolCall.getFunction()
                                        );
                                        case "todo_delete" -> handleTodoDeleteFunction(
                                                event.getDto().chatId(),
                                                event.getDto().user(),
                                                toolCall.getFunction()
                                        );
                                        default -> throw new RuntimeException(
                                                "Unknown function: "
                                                + toolCall.getFunction().getName()
                                        );

                                    };
                                } catch (Exception error) {
                                    throw new RuntimeException(error);
                                }
                            }
                    ).forEach(s -> this.telegramBotService.sendMessage(
                            event.getDto().chatId(),
                            s
                    ));
        } else {
            this.handleBotMessage(
                    event.getDto().chatId(),
                    event.getDto().choice().getMessage().getContent()
            );
        }
    }

    private String handleTodoAddFunction(
            long chatId,
            User user,
            Message.ToolCall.Function function
    ) throws JsonProcessingException {
        TodoAddArgumentsDto arg = this.jsonUtilService.getTodoAddArguments(
                function.getArguments()
        );

        Task t = new Task();
        t.setDescription(arg.title());
        t.setChatId(chatId);
        t.setUser(user);
        this.taskService.saveTask(t);

        return "Task %s created".formatted(t.getId());
    }

    private String handleTodoDeleteFunction(
            long chatId,
            User user,
            Message.ToolCall.Function function
    ) throws JsonProcessingException {
        TodoDeleteArgumentsDto arg = this.jsonUtilService.getTodoDeleteArguments(
                function.getArguments()
        );

        this.taskService.deleteTask(arg.id());

        return "Task %s deleted".formatted(arg.id());
    }

    private void handleBotMessage(long chatId, String message) {
        telegramBotService.sendMessage(
                chatId,
                message
        );
    }

    public record TodoAddArgumentsDto(
            String title
    ) {
    }

    public record TodoDeleteArgumentsDto(
            UUID id
    ) {
    }
}
