package com.hackaton.vk_mini_backend.dto;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
@Builder(toBuilder = true)
public class ClsUserDto {
    private Long id;
    private Long idActivationStatus;
    private String name;
    private String login;
    private String password;
    private Boolean isDeleted;
    private Timestamp timeCreate;

    public String getLogin() {
        return StringUtils.isBlank(this.login) ? null : this.login.trim();
    }

    public String getPassword() {
        return StringUtils.isBlank(this.password) ? null : this.password;
    }
}
