package com.hackaton.vk_mini_backend.controller;

import com.hackaton.vk_mini_backend.dto.ChatRequestDto;
import com.hackaton.vk_mini_backend.dto.request.RecommendationRequestDto;
import com.hackaton.vk_mini_backend.service.YandexApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Чат", description = "Контроллер чата с нейросетью Yandex")
public class YandexController {

    private final YandexApiService yandexApiService;

    @PostMapping("/api/chat")
    @Operation(summary = "Обработка запроса в чат", description = "Отправляет сообщения в YandexGPT и возвращает ответ")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody ChatRequestDto request) {
        ResponseEntity<Map<String, Object>> responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        try {
            Map<String, Object> result = yandexApiService.processChatRequest(request);
            HttpStatus status = result.containsKey("error") ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            responseEntity = ResponseEntity.status(status).body(result);
        } catch (Exception ex) {
            log.error("Ошибка при обработке чат-запроса: {}", ex.getMessage(), ex);
        }
        return responseEntity;
    }

    @PostMapping("/api/recommendations")
    @Operation(
            summary = "Получение рекомендаций курсов",
            description = "Формирует рекомендации курсов на основе профиля пользователя и списка курсов")
    public ResponseEntity<Map<String, Object>> recommendations(@RequestBody RecommendationRequestDto request) {
        ResponseEntity<Map<String, Object>> responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        try {
            Map<String, Object> result = yandexApiService.processRecommendations(request);
            HttpStatus status = result.containsKey("error") ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            responseEntity = ResponseEntity.status(status).body(result);
        } catch (Exception ex) {
            log.error("Ошибка при получении рекомендаций: {}", ex.getMessage(), ex);
        }
        return responseEntity;
    }
}
