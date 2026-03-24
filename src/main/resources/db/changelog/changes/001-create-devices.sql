--liquibase formatted sql

--changeset mlasota:20260324-01-create-devices
CREATE TABLE devices (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255),
    state VARCHAR(255) NOT NULL DEFAULT 'available',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_state_values CHECK (state IN ('available', 'in-use', 'inactive'))
);
--rollback DROP TABLE devices;