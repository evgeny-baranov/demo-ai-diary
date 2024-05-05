package com.example.demo.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionRequest implements Serializable {
    private String model;
    private MultipartFile file;

    public TranscriptionRequest(MultipartFile file) {
        this.file = file;
    }
}
