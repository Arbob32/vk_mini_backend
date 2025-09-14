package com.hackaton.vk_mini_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackaton.vk_mini_backend.dto.MessageDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class YandexResponse {
    @JsonProperty("result")
    private Result result;

    @Data
    public static class Result {
        @JsonProperty("alternatives")
        private List<Alternative> alternatives;
    }

    @Data
    public static class Alternative {
        @JsonProperty("message")
        private MessageDto message;
    }
}
