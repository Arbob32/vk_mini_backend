package com.hackaton.vk_mini_backend.dto.request;

import com.hackaton.vk_mini_backend.dto.CourseDto;
import com.hackaton.vk_mini_backend.dto.UserProfileDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class RecommendationRequestDto {
    private UserProfileDto userProfile;
    private List<CourseDto> courses;
}
