package com.example.demo.openai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    private String tool_call_id;
    private MessageRole role;
    private String name;
    private Object content;
    private List<ToolCall> tool_calls;

    public Message(MessageRole role, String content) {
        this.role = role;
        this.content = content;
    }

    public Message(ToolCall toolCall, Object content) throws JsonProcessingException {
        this.role = MessageRole.tool;
        this.name = toolCall.function.name;
        this.tool_call_id = toolCall.id;
        this.content = new ObjectMapper().writeValueAsString(content);
    }

    public Message(List<ContentItem> content) {
        this.role = MessageRole.user;
        this.content = content;
    }

    public enum MessageRole {
        user,
        assistant,
        system,
        function,
        tool,
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContentItem {
        Type type = Type.text;
        String text;
        ImageUrl image_url;

        public ContentItem(String text) {
            this.text = text;
        }

        public ContentItem(ImageUrl imageUrl) {
            this.type = Type.image_url;
            this.image_url = imageUrl;
        }

        public enum Type {text, image_url}

        public record ImageUrl(String url) {
        }
    }

    @Data
    @AllArgsConstructor
    public static class ToolCallResult {
        private String type;
        private Object content;
    }

    @Data
    public static class ToolCall {
        private String id;
        private String type;
        private Function function;

        @Data
        public static class Function {
            private String name;
            private String arguments;
        }
    }
}
