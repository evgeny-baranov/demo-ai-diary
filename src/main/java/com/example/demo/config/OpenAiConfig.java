package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "openai")
@Data
public class OpenAiConfig {
    private String organisation;
    private String apiKey;
    private String baseUrl;
    private Models models;

    @Data
    public static class Models {
        private Model completions;
        private Model vision;
        private Model transcriptions;
    }

    @Data
    public static class Model {
        private String name;
        private String url;
        private String prompt;
    }
}
