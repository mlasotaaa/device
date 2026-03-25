--liquibase formatted sql
--changeset mlasota:003-update-device-state-constraints-to-uppercase

ALTER TABLE devices DROP CONSTRAINT check_state_values;

UPDATE devices SET state = 'AVAILABLE' WHERE state = 'available';
UPDATE devices SET state = 'IN_USE' WHERE state = 'in-use';
UPDATE devices SET state = 'INACTIVE' WHERE state = 'inactive';

ALTER TABLE devices ADD CONSTRAINT check_state_values
    CHECK (state IN ('AVAILABLE', 'IN_USE', 'INACTIVE'));

--rollback ALTER TABLE devices DROP CONSTRAINT check_state_values;
--rollback UPDATE devices SET state = 'available' WHERE state = 'AVAILABLE';
--rollback UPDATE devices SET state = 'in-use' WHERE state = 'IN_USE';
--rollback UPDATE devices SET state = 'inactive' WHERE state = 'INACTIVE';
--rollback ALTER TABLE devices ADD CONSTRAINT check_state_values CHECK (state IN ('available', 'in-use', 'inactive'));