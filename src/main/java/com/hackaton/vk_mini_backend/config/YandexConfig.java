package com.hackaton.vk_mini_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YandexConfig {
    @Value("${frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${yandex.api-key:#{null}}")
    private String apiKey;

    @Value("${yandex.folder-id:#{null}}")
    private String folderId;
}
