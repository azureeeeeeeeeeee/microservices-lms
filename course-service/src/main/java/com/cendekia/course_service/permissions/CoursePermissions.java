package com.cendekia.course_service.permissions;


import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cendekia.course_service.exceptions.AccessDeniedException;
import com.cendekia.course_service.models.Course;

@Service
public class CoursePermissions {
    // canAction()
    public boolean canCreate(String role) {
        return role.equals("ADMIN");
    }

    public boolean canUpdate(String role, String userId, Course course) {
        if (role.equals("ADMIN")) {
            return true;
        }

        return course.getInstructorId().equals(UUID.fromString(userId));
    }



    // checkAction()
    public void checkCreate(String role) {
        if (!this.canCreate(role)) {
            throw new AccessDeniedException("You do not have permission to create new course");
        }
    }

    public void checkUpdate(String role, String userId, Course course) {
        if (!this.canUpdate(role, userId, course)) {
            throw new AccessDeniedException("You do not have permission to this course");
        }
    }
}
