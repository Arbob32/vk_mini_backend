package com.hackaton.vk_mini_backend.dto.response;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Long id;
    private String login;
    private String name;

    public JwtResponse(
            final String accessToken, final String refreshToken, final Long id, final String login, final String name) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.login = login;
        this.name = name;
    }
}
