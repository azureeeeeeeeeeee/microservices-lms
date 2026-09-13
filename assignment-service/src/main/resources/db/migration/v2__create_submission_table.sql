CREATE TABLE submissions (
    id UUID PRIMARY KEY,

    assignment_id UUID NOT NULL,
    student_id UUID NOT NULL,

    score INT NOT NULL,

    submitted_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_submission_assignment
        FOREIGN KEY (assignment_id)
        REFERENCES assignments(id)
        ON DELETE CASCADE
)