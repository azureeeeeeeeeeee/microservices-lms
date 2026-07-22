CREATE TABLE courses (
    id UUID PRIMARY KEY,

    title VARCHAR(255) UNIQUE NOT NULL,
    description TEXT NOT NULL,

    instructor_id UUID NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)