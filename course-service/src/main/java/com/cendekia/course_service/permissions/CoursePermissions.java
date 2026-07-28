package com.cendekia.course_service.permissions;


import org.springframework.stereotype.Service;

import com.cendekia.course_service.exceptions.AccessDeniedException;

@Service
public class CoursePermissions {
    // canAction()
    public boolean canCreate(String role) {
        return role.equals("ADMIN");
    }



    // checkAction()
    public void checkCreate(String role) {
        if (!this.canCreate(role)) {
            throw new AccessDeniedException("You do not have permission to create new course");
        }
    }
}
