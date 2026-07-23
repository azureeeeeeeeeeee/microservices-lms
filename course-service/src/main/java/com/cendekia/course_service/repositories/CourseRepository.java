package com.cendekia.course_service.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cendekia.course_service.models.Course;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    
}
