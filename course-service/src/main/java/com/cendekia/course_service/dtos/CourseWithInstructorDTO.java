package com.cendekia.course_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseWithInstructorDTO {
    private CourseDTO course;
    private InstructorDTO instructor;
}
