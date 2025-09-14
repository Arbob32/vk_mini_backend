package com.hackaton.vk_mini_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackaton.vk_mini_backend.dto.MessageDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder(toBuilder = true)
public class YandexRequest {
    @JsonProperty("modelUri")
    private String modelUri;

    @JsonProperty("completionOptions")
    private Map<String, Object> completionOptions;

    @JsonProperty("messages")
    private List<MessageDto> messages;
}
