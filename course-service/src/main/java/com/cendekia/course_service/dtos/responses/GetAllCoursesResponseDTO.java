package com.cendekia.course_service.dtos.responses;

import java.util.List;

import com.cendekia.course_service.dtos.CourseWithInstructorDTO;

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
public class GetAllCoursesResponseDTO {
    private String message;
    private List<CourseWithInstructorDTO> data;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
