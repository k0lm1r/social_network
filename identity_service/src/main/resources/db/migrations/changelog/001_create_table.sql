--liquibase formatted sql

--changeset Andrey:1
--comment: users table creating
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    keycloak_id VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    display_name VARCHAR(50) NOT NULL,
    bio VARCHAR(1000),
    role VARCHAR(20) NOT NULL,
    is_enabled BOOLEAN DEFAULT TRUE,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);