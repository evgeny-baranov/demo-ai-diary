package com.example.demo.message;

import com.example.demo.message.openai.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class CompletionResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private int index;
        private Message message;
        private String finish_reason;
    }

    private String id;
    private String object;

}
// {
//  "id": "chatcmpl-abc123",
//  "object": "chat.completion",
//  "created": 1699896916,
//  "model": "gpt-3.5-turbo-0125",
//  "choices": [
//    {
//      "index": 0,
//      "message": {
//        "role": "assistant",
//        "content": null,
//        "tool_calls": [
//          {
//            "id": "call_abc123",
//            "type": "function",
//            "function": {
//              "name": "get_current_weather",
//              "arguments": "{\n\"location\": \"Boston, MA\"\n}"
//            }
//          }
//        ]
//      },
//      "logprobs": null,
//      "finish_reason": "tool_calls"
//    }
//  ],
//  "usage": {
//    "prompt_tokens": 82,
//    "completion_tokens": 17,
//    "total_tokens": 99
//  }
//}
