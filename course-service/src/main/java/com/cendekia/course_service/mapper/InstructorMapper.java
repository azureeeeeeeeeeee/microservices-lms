package com.cendekia.course_service.mapper;

import com.cendekia.course_service.dtos.InstructorDTO;
import com.cendekia.user.grpc.UserResponse;

public class InstructorMapper {
    public static InstructorDTO toDTO(UserResponse instructorResponse) {
        InstructorDTO instructor = new InstructorDTO();

        instructor.setEmail(instructorResponse.getEmail());
        instructor.setFullname(instructorResponse.getFullname());
        instructor.setId(instructorResponse.getId());
        instructor.setRole(instructorResponse.getRole());

        return instructor;
    } 
}
