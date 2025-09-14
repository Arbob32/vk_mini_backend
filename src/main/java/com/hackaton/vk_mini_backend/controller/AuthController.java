package com.hackaton.vk_mini_backend.controller;

import com.hackaton.vk_mini_backend.dto.request.*;
import com.hackaton.vk_mini_backend.dto.response.JwtResponse;
import com.hackaton.vk_mini_backend.dto.response.MessageResponse;
import com.hackaton.vk_mini_backend.dto.response.TokenRefreshResponse;
import com.hackaton.vk_mini_backend.security.vk.VkSignVerifier;
import com.hackaton.vk_mini_backend.service.AuthService;
import com.hackaton.vk_mini_backend.service.VkAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Методы для аутентификации пользователей")
public class AuthController {

    private final AuthService authService;

    private final VkAuthService vkAuthService;

    private final VkSignVerifier vkSignVerifier;

    @Value("${vk.auth.enableServiceLogin:false}")
    private boolean enableServiceLogin;

    @PostMapping("/login")
    @Operation(
            summary = "Аутентификация пользователя",
            description = "Позволяет пользователю войти в систему и получить JWT токен")
    public ResponseEntity<JwtResponse> authenticateUser(
            @Parameter(description = "Данные для входа", required = true) @Valid @RequestBody
                    final LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя", description = "Создаёт нового пользователя в системе")
    public ResponseEntity<MessageResponse> registerUser(
            @Parameter(description = "Данные для регистрации", required = true) @Valid @RequestBody
                    final RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/refreshtoken")
    @Operation(
            summary = "Обновление JWT токена",
            description = "Позволяет получить новый токен на основе refresh token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(
            @Parameter(description = "Refresh токен", required = true) @Valid @RequestBody
                    final RefreshTokenRequest request) {
        TokenRefreshResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/vk-service-user")
    @Operation(
            summary = "VK Service Auth with User ID",
            description = "Авторизация через сервисный ключ VK с указанием ID пользователя")
    public ResponseEntity<JwtResponse> authenticateWithVkServiceAndUserId(
            @Valid @RequestBody VkServiceLoginRequest request) {
        ResponseEntity<JwtResponse> response;
        if (enableServiceLogin) {
            try {
                JwtResponse jwtResponse = vkAuthService.loginWithVkServiceKeyAndUserId(request.getUserId());
                response = ResponseEntity.ok(jwtResponse);
            } catch (Exception e) {
                log.error("Ошибка аутентификации для пользователя VK ID: {}", request.getUserId(), e);
                response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            response = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return response;
    }

    @GetMapping("/vk/verify")
    @Operation(
            summary = "Проверка подписи VK Mini App",
            description = "В дев-режиме возвращает vk_user_id, в проде используется для диагностики")
    public ResponseEntity<MessageResponse> verifyVkSign(@RequestParam(required = false) String query) {
        ResponseEntity<MessageResponse> response;
        String raw = query;
        if (raw == null) {
            response = ResponseEntity.badRequest().body(new MessageResponse("Параметр query обязателен"));
        } else {
            var params = VkSignVerifier.parseQueryToMap(raw);
            boolean ok = vkSignVerifier.validateSignature(params) && vkSignVerifier.validateTimestamp(params);
            Long userId = vkSignVerifier.extractUserId(params);
            response = ResponseEntity.ok(
                    new MessageResponse("valid=" + ok + ", userId=" + (userId == null ? "null" : userId)));
        }
        return response;
    }

    @PostMapping("/login/vk-launch")
    @Operation(
            summary = "Логин по VK Mini App launch params",
            description = "Валидирует подпись и создаёт JWT на пользователя vk_#{vk_user_id}")
    public ResponseEntity<JwtResponse> loginByLaunchParams(@Valid @RequestBody VkLaunchLoginRequest request) {
        ResponseEntity<JwtResponse> response;
        var params = VkSignVerifier.parseQueryToMap(request.getLaunchQuery());
        boolean ok = vkSignVerifier.validateSignature(params) && vkSignVerifier.validateTimestamp(params);
        if (ok) {
            Long userId = vkSignVerifier.extractUserId(params);
            if (userId != null) {
                try {
                    JwtResponse jwtResponse = vkAuthService.loginWithVkServiceKeyAndUserId(userId);
                    response = ResponseEntity.ok(jwtResponse);
                } catch (Exception e) {
                    log.error("Ошибка логина по launch params, userId={}", userId, e);
                    response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            } else {
                response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            response = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return response;
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход из системы", description = "Завершает сеанс пользователя и удаляет refresh токен")
    public ResponseEntity<MessageResponse> logoutUser() {
        String username = null;
        Object principal =
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        }
        MessageResponse response;
        if (username != null) {
            response = authService.logout(username);
        } else {
            response = new MessageResponse("Вы не авторизованы");
        }
        return ResponseEntity.ok(response);
    }
}
