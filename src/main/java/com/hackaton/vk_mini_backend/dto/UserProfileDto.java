package com.hackaton.vk_mini_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class UserProfileDto {
    private String firstName;
    private String lastName;
    private Integer age;
    private Integer sex;
    private String city;
    private List<String> interests;
}
