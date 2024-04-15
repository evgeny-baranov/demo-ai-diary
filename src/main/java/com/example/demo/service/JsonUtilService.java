package com.example.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JsonUtilService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CommandDispatcher.TodoAddArgumentsDto getTodoAddArguments(
            String json
    ) throws JsonProcessingException {
        return objectMapper.readValue(
                json,
                CommandDispatcher.TodoAddArgumentsDto.class
        );
    }

    public CommandDispatcher.TodoDeleteArgumentsDto getTodoDeleteArguments(
            String json
    ) throws JsonProcessingException {
        return objectMapper.readValue(
                json,
                CommandDispatcher.TodoDeleteArgumentsDto.class
        );
    }
}
