package com.cendekia.course_service.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cendekia.course_service.dtos.CourseDTO;
import com.cendekia.course_service.dtos.GetCourseResponseDTO;
import com.cendekia.course_service.services.CourseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/{id}")
    public ResponseEntity<GetCourseResponseDTO> getSingleCourse(@PathVariable UUID id) {
        CourseDTO dto = courseService.getSingleCourse(id);
        GetCourseResponseDTO response = new GetCourseResponseDTO();

        String message = String.format("Course fetched successfully [Title: %s, ID: %s]", dto.getTitle(), dto.getId());
        response.setMesssage(message);
        response.setCourse(dto);

        return ResponseEntity.ok(response);
    }
}
