package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.openai.Message;
import com.example.demo.openai.TranscriptionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatGptService {
    void makeUserCompletionRequest(
            long chatId,
            User user,
            String message
    );

    void makeCompletionRequest(
            long chatId,
            User user,
            List<Message> messageList
    );

    void makeCompletionRequest(
            long chatId,
            User user,
            Message message
    );

    TranscriptionResponse makeWhisperRequest(byte[] audioData);
}
