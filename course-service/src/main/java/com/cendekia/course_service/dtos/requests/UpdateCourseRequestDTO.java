package com.cendekia.course_service.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCourseRequestDTO {
    @Size(min = 8, max = 255, message = "Title has to be between 8 and 255 characters")
    @NotBlank(message = "Title is required")
    private String title;
    
    @Size(min = 30, max = 2000, message = "Description has to be between 30 and 2000 characters")
    @NotBlank(message = "Description is required")
    private String description;
}
