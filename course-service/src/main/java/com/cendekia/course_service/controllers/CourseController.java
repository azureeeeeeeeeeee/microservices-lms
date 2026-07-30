package com.cendekia.course_service.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cendekia.course_service.dtos.CourseDTO;
import com.cendekia.course_service.dtos.requests.CreateCourseRequestDTO;
import com.cendekia.course_service.dtos.requests.UpdateCourseRequestDTO;
import com.cendekia.course_service.dtos.responses.CreateCourseResponseDTO;
import com.cendekia.course_service.dtos.responses.GetCourseResponseDTO;
import com.cendekia.course_service.dtos.responses.UpdateCourseResponseDTO;
import com.cendekia.course_service.dtos.responses.GetAllCoursesResponseDTO;
import com.cendekia.course_service.services.CourseService;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
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
        GetCourseResponseDTO response = courseService.getSingleCourse(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<GetAllCoursesResponseDTO> getAllCourses(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        GetAllCoursesResponseDTO response = courseService.getAllCourses(page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<CreateCourseResponseDTO> createCourse(
        @Valid @RequestBody CreateCourseRequestDTO createCourseRequestDTO,
        @RequestHeader("X-USER-ID") String userId,
        @RequestHeader("X-USER-ROLE") String userRole
    ) {
        CourseDTO course = courseService.createCourse(
            createCourseRequestDTO,
            userId,
            userRole
        );
        
        CreateCourseResponseDTO response = new CreateCourseResponseDTO();
        response.setMessage("Course Created Successfully");
        response.setCourse(course);
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UpdateCourseResponseDTO> updateCourse(
        @Valid @RequestBody UpdateCourseRequestDTO updateCourseRequestDTO,
        @PathVariable UUID id,
        @RequestHeader("X-USER-ID") String userId,
        @RequestHeader("X-USER-ROLE") String userRole
    ) {
        CourseDTO course = courseService.updateCourse(updateCourseRequestDTO, id.toString(), userRole, userId);

        UpdateCourseResponseDTO response = new UpdateCourseResponseDTO();
        response.setMessage(String.format("Course updated successfuly [ID : %s]", id));
        response.setCourse(course);

        return ResponseEntity.ok(response);
    }
}
