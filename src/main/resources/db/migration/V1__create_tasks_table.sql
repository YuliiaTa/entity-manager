CREATE TABLE entity (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deadline TIMESTAMP,
    CONSTRAINT valid_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED')),
    CONSTRAINT valid_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
);