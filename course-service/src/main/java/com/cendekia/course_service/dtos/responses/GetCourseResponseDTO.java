package com.cendekia.course_service.dtos.responses;

import com.cendekia.course_service.dtos.CourseDTO;
import com.cendekia.course_service.dtos.InstructorDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetCourseResponseDTO {
    private String messsage;
    private CourseDTO course;
    private InstructorDTO instructor;
}
