CREATE TABLE refresh_token (
    user_uuid UUID PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
);
