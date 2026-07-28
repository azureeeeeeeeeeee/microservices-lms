package com.cendekia.course_service.dtos.responses;

import com.cendekia.course_service.dtos.CourseDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCourseResponseDTO {
    private String message;
    private CourseDTO course;
}
