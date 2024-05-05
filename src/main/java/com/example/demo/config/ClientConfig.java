package com.example.demo.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ClientConfig {
    @Autowired
    OpenAiConfig openAiConfig;

    @Bean
    public Logger.Level feignLogger() {
        return Logger.Level.FULL;
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default();
    }

    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        return request -> {
            request.header(
                    "Authorization",
                    "Bearer " + openAiConfig.getApiKey()
            );
        };
    }
}
