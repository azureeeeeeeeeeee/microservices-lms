package com.cendekia.assignment_service.permissions;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cendekia.assignment_service.clients.EnrollmentClient;
import com.cendekia.assignment_service.exceptions.AccessDeniedException;
import com.cendekia.assignment_service.models.Submission;
import com.cendekia.course.grpc.CourseResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssignmentPermissions {

    private final EnrollmentClient enrollmentClient;

    // ─── canXxx helpers ───────────────────────────────────────────────────────

    /** Teacher of the course can create/update/delete assignments */
    public boolean canManageAssignment(String role, String userId, CourseResponse course) {
        return role.equals("TEACHER") && course.getInstructorId().equals(userId);
    }

    /** Students enrolled in the course; teacher of the course; admin */
    public boolean canReadAssignments(String role, String userId, UUID courseId) {
        if (role.equals("ADMIN")) return true;
        if (role.equals("TEACHER")) return true; // any teacher can read (could tighten later)
        if (role.equals("STUDENT")) {
            return enrollmentClient.isEnrolled(courseId, UUID.fromString(userId));
        }
        return false;
    }

    /** Only enrolled students can submit */
    public boolean canSubmit(String role, String userId, UUID courseId) {
        return role.equals("STUDENT") && enrollmentClient.isEnrolled(courseId, UUID.fromString(userId));
    }

    /** Submission owner or the teacher of the course */
    public boolean canViewSubmission(String role, String userId, Submission submission, CourseResponse course) {
        if (role.equals("TEACHER") && course.getInstructorId().equals(userId)) return true;
        if (role.equals("ADMIN")) return true;
        return submission.getStudentId().toString().equals(userId);
    }

    /** Only the teacher of the course can grade */
    public boolean canGrade(String role, String userId, CourseResponse course) {
        return role.equals("TEACHER") && course.getInstructorId().equals(userId);
    }

    // ─── checkXxx guard methods ────────────────────────────────────────────────

    public void checkManageAssignment(String role, String userId, CourseResponse course) {
        if (!canManageAssignment(role, userId, course)) {
            throw new AccessDeniedException("You do not have permission to manage assignments for this course");
        }
    }

    public void checkReadAssignments(String role, String userId, UUID courseId) {
        if (!canReadAssignments(role, userId, courseId)) {
            throw new AccessDeniedException("You must be enrolled in this course to view its assignments");
        }
    }

    public void checkSubmit(String role, String userId, UUID courseId) {
        if (!canSubmit(role, userId, courseId)) {
            throw new AccessDeniedException("You must be enrolled in this course to submit an assignment");
        }
    }

    public void checkViewSubmission(String role, String userId, Submission submission, CourseResponse course) {
        if (!canViewSubmission(role, userId, submission, course)) {
            throw new AccessDeniedException("You do not have permission to view this submission");
        }
    }

    public void checkGrade(String role, String userId, CourseResponse course) {
        if (!canGrade(role, userId, course)) {
            throw new AccessDeniedException("You do not have permission to grade submissions for this course");
        }
    }
}
