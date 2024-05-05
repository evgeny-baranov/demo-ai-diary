package com.example.demo.openai;
import com.example.demo.config.ClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "openai-service",
        url = "${openai.base-url}",
        configuration = ClientConfig.class
)
public interface OpenAiClient {
    @PostMapping(
            value = "${openai.models.transcriptions.url}",
            headers = {"Content-Type=multipart/form-data"}
    )
    TranscriptionResponse createTranscription(
            @ModelAttribute TranscriptionRequest whisperRequest
    );

    @PostMapping(
            value = "${openai.models.completions.url}",
            headers = {"Content-Type=application/json"}
    )
    CompletionResponse createCompletion(
            @RequestBody CompletionRequest completionRequest
    );
}
