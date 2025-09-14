package com.hackaton.vk_mini_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VkServiceLoginRequest {
    @NotNull
    private Long userId;
}
