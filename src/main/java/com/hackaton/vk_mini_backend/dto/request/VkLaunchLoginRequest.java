package com.hackaton.vk_mini_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class VkLaunchLoginRequest {

    @NotBlank
    private String launchQuery;

    public VkLaunchLoginRequest(final String launchQuery) {
        this.launchQuery = launchQuery;
    }

    public String getLaunchQuery() {
        return launchQuery;
    }

    public void setLaunchQuery(final String launchQuery) {
        this.launchQuery = launchQuery;
    }
}
