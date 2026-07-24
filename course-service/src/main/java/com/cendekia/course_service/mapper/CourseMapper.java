package com.cendekia.course_service.mapper;

import com.cendekia.course_service.dtos.CourseDTO;
import com.cendekia.course_service.models.Course;

public class CourseMapper {
    public static CourseDTO toDTO(Course course) {
        CourseDTO dto = new CourseDTO();

        dto.setId(course.getId().toString());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setInstructorId(course.getInstructorId().toString());

        return dto;
    }
}
