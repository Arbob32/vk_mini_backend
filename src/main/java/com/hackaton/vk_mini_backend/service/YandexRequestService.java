package com.hackaton.vk_mini_backend.service;

import com.hackaton.vk_mini_backend.dto.MessageDto;
import com.hackaton.vk_mini_backend.dto.request.YandexRequest;
import com.hackaton.vk_mini_backend.dto.response.YandexResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class YandexRequestService {
    @Value("${yandex.api-key}")
    private String apiKey;

    @Value("${yandex.folder-id}")
    private String folderId;

    private final RestTemplate restTemplate = new RestTemplate();

    public String callYandexGpt(List<MessageDto> messages, double temperature, int maxTokens) throws Exception {
        if (apiKey == null || folderId == null) {
            throw new Exception("Не указаны API включи для YandexGPT");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Api-Key " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> completionOptions = new HashMap<>();
        completionOptions.put("stream", false);
        completionOptions.put("temperature", temperature);
        completionOptions.put("maxTokens", maxTokens);

        YandexRequest requestBody = YandexRequest.builder()
                .messages(messages)
                .completionOptions(completionOptions)
                .modelUri("gpt://" + folderId + "/yandexgpt/latest")
                .build();

        log.info("🔄 Отправка запроса в YandexGPT");
        log.info("📝 Число сообщений: {}", messages.size());

        HttpEntity<YandexRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<YandexResponse> response = restTemplate.exchange(
                    "https://llm.api.cloud.yandex.net/foundationModels/v1/completion",
                    HttpMethod.POST,
                    entity,
                    YandexResponse.class);

            String responseText = Optional.ofNullable(response.getBody())
                    .map(YandexResponse::getResult)
                    .map(YandexResponse.Result::getAlternatives)
                    .filter(alts -> !alts.isEmpty())
                    .map(List::getFirst)
                    .map(YandexResponse.Alternative::getMessage)
                    .map(MessageDto::getText)
                    .orElse("");

            if (responseText.isEmpty()) {
                log.error("❌ Пустой ответ от YandexGPT: {}", response.getBody());
                throw new Exception("Пустой ответ от YandexGPT");
            }

            log.info("✅ Получен ответ от YandexGPT, длина: {}", responseText.length());
            return responseText;

        } catch (HttpClientErrorException e) {
            log.error("❌ YandexGPT API ошибка: статус={}, ответ={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (RestClientException e) {
            log.error("❌ Ошибка при запросе в YandexGPT: {}", e.getMessage());
            throw e;
        }
    }
}
