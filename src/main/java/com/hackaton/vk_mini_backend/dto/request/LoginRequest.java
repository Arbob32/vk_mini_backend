package com.hackaton.vk_mini_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String login;

    @NotBlank
    private String password;
}
