package com.hackaton.vk_mini_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class ChatRequestDto {
    private List<MessageDto> messages;
}
