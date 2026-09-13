ALTER TABLE enrollments
    ADD CONSTRAINT uq_enrollments_user_course UNIQUE (user_id, course_id);
