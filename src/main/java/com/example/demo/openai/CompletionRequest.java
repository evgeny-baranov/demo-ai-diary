package com.example.demo.openai;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompletionRequest {
    @NonNull
    private String model;
    private List<Message> messages = new ArrayList<>();
    private List<FunctionTool> tools = new ArrayList<>();

    public void addTool(FunctionTool tool) {
        this.tools.add(tool);
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }
}
