--liquibase formatted sql
--changeset mlasota:002-change-device-uuid-column-to-device-id

ALTER TABLE devices RENAME COLUMN uuid TO device_id;

--rollback ALTER TABLE devices RENAME COLUMN device_id TO uuid;