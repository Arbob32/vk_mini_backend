package com.hackaton.vk_mini_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String login;

    @NotBlank
    private String name;

    @NotBlank
    private String password;
}
