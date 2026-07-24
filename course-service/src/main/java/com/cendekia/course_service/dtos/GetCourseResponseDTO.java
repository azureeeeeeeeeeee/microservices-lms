package com.cendekia.course_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetCourseResponseDTO {
    private String messsage;
    private CourseDTO course;
}
