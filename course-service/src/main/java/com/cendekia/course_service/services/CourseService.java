package com.cendekia.course_service.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cendekia.course_service.dtos.CourseDTO;
import com.cendekia.course_service.exceptions.InvalidCourseException;
import com.cendekia.course_service.mapper.CourseMapper;
import com.cendekia.course_service.models.Course;
import com.cendekia.course_service.repositories.CourseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    
    public CourseDTO getSingleCourse(UUID id) {
        Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new InvalidCourseException(
                        String.format("Course not found with given ID : %s", id.toString())
                    ));

        return CourseMapper.toDTO(course);
    }

    public CourseDTO createCourse(String title, String description, UUID instructorId, UUID createdBy) {
        // TODO : Add Authorization

        Course course = Course.builder()
            .title(title)
            .description(description)
            .instructorId(instructorId)
            .createdBy(createdBy)
            .createdAt(Instant.now())
            .build();

        return CourseMapper.toDTO(courseRepository.save(course));
    }
}
