CREATE TABLE assignments (
    id UUID PRIMARY KEY,

    course_id UUID NOT NULL,

    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,

    deadline TIMESTAMPTZ NOT NULL
)