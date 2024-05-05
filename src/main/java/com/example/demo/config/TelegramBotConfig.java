package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Data
@ConfigurationProperties(prefix = "bot")
public class TelegramBotConfig {
    private String name;
    private String token;
    private Long ownerId;
    private String greetings;
}
