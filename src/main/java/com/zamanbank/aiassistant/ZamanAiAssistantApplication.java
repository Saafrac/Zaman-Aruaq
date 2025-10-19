package com.zamanbank.aiassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ZamanAiAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZamanAiAssistantApplication.class, args);
    }

}

