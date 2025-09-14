package com.hackaton.vk_mini_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CourseDto {
    private String id;
    private String title;
    private String category;
}
