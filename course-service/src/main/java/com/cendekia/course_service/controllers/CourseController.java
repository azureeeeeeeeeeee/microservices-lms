package com.cendekia.course_service.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cendekia.course_service.dtos.CourseDTO;
import com.cendekia.course_service.dtos.requests.CreateCourseRequestDTO;
import com.cendekia.course_service.dtos.responses.CreateCourseResponseDTO;
import com.cendekia.course_service.dtos.responses.GetCourseResponseDTO;
import com.cendekia.course_service.services.CourseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/{id}/")
    public ResponseEntity<GetCourseResponseDTO> getSingleCourse(@PathVariable UUID id) {
        CourseDTO dto = courseService.getSingleCourse(id);
        GetCourseResponseDTO response = new GetCourseResponseDTO();

        String message = String.format("Course fetched successfully [Title: %s, ID: %s]", dto.getTitle(), dto.getId());
        response.setMesssage(message);
        response.setCourse(dto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/")
    public ResponseEntity<CreateCourseResponseDTO> createCourse(
        @Valid @RequestBody CreateCourseRequestDTO createCourseRequestDTO,
        @RequestHeader("X-USER-ID") String userId
    ) {
        CourseDTO course = courseService.createCourse(
            createCourseRequestDTO.getTitle(),
            createCourseRequestDTO.getDescription(),
            createCourseRequestDTO.getInstructorId(),
            UUID.fromString(userId)
        );

        CreateCourseResponseDTO response = new CreateCourseResponseDTO();
        response.setMessage("Course Created Successfully");
        response.setCourse(course);

        return ResponseEntity.ok(response);
    }
}
