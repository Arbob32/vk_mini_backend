package com.hackaton.vk_mini_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class MessageDto {
    private String role;
    private String text;
}
