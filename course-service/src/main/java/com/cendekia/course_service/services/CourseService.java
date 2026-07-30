package com.cendekia.course_service.services;


import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cendekia.course_service.dtos.CourseDTO;
import com.cendekia.course_service.dtos.requests.CreateCourseRequestDTO;
import com.cendekia.course_service.dtos.requests.UpdateCourseRequestDTO;
import com.cendekia.course_service.dtos.responses.GetCourseResponseDTO;
import com.cendekia.course_service.exceptions.InvalidCourseException;
import com.cendekia.course_service.grpc.UserGrpcClient;
import com.cendekia.course_service.mapper.CourseMapper;
import com.cendekia.course_service.mapper.InstructorMapper;
import com.cendekia.course_service.models.Course;
import com.cendekia.course_service.permissions.CoursePermissions;
import com.cendekia.course_service.repositories.CourseRepository;
import com.cendekia.course_service.dtos.responses.GetAllCoursesResponseDTO;
import com.cendekia.course_service.dtos.CourseWithInstructorDTO;
import com.cendekia.course_service.dtos.InstructorDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;
import com.cendekia.user.grpc.UserResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserGrpcClient userGrpcClient;
    private final CoursePermissions coursePermissions;
    
    public GetCourseResponseDTO getSingleCourse(UUID id) {
        Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new InvalidCourseException(
                        String.format("Course not found with given ID : %s", id.toString())
                    ));

        UserResponse instructor = userGrpcClient.getUser(course.getInstructorId());

        GetCourseResponseDTO response = new GetCourseResponseDTO();
        response.setMesssage(String.format("Course fetched successfully [Title: %s, ID: %s]", course.getTitle(), course.getId()));
        response.setCourse(CourseMapper.toDTO(course));
        response.setInstructor(InstructorMapper.toDTO(instructor));

        return response;
    }

    public GetAllCoursesResponseDTO getAllCourses(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Course> courses = courseRepository.findAll(pageable);

        List<CourseWithInstructorDTO> courseWithInstructorDTOs = courses.getContent().stream().map(course -> {
            InstructorDTO instructorDTO = null;
            try {
                UserResponse instructor = userGrpcClient.getUser(course.getInstructorId());
                instructorDTO = InstructorMapper.toDTO(instructor);
            } catch (Exception e) {
                log.warn("Failed to fetch instructor [ID: {}] for course [ID: {}]: {}",
                        course.getInstructorId(), course.getId(), e.getMessage());
            }
            return CourseWithInstructorDTO.builder()
                    .course(CourseMapper.toDTO(course))
                    .instructor(instructorDTO)
                    .build();
        }).collect(Collectors.toList());

        return GetAllCoursesResponseDTO.builder()
                .message("Courses fetched successfully")
                .data(courseWithInstructorDTOs)
                .pageNo(courses.getNumber())
                .pageSize(courses.getSize())
                .totalElements(courses.getTotalElements())
                .totalPages(courses.getTotalPages())
                .last(courses.isLast())
                .build();
    }

    public CourseDTO createCourse(CreateCourseRequestDTO createCourseRequestDTO, String userId, String role) {
        coursePermissions.checkCreate(role);

        Course course = Course.builder()
            .title(createCourseRequestDTO.getTitle())
            .description(createCourseRequestDTO.getDescription())
            .instructorId(createCourseRequestDTO.getInstructorId())
            .createdBy(UUID.fromString(userId))
            .createdAt(Instant.now())
            .build();

        return CourseMapper.toDTO(courseRepository.save(course));
    }


    public CourseDTO updateCourse(UpdateCourseRequestDTO updateCourseRequestDTO, String courseId, String userRole, String userId) {
        Course course = courseRepository.findById(UUID.fromString(courseId))
            .orElseThrow(() -> new InvalidCourseException(String.format("Course not exists [ID : %s]", courseId)));
        
        coursePermissions.checkUpdate(userRole, userId, course);

        course.setTitle(updateCourseRequestDTO.getTitle());
        course.setDescription(updateCourseRequestDTO.getDescription());

        Course updatedCourse = courseRepository.save(course);

        return CourseMapper.toDTO(updatedCourse);
    }
}