package com.cendekia.enrollment_service.models;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "course_id", nullable = false)
    private UUID courseId;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private Instant enrolledAt;
}
