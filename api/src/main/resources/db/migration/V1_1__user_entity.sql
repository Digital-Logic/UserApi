SET TIME ZONE 'UTC';

CREATE TABLE user_entity
(
    id         UUID         NOT NULL PRIMARY KEY,
    email      VARCHAR(80)  NOT NULL,
    password   VARCHAR(160) NOT NULL,
    first_name VARCHAR(40),
    last_name  VARCHAR(40),
    archived   BOOLEAN      NOT NULL DEFAULT FALSE,

    /* OPT LOCK */
    version    INT          NOT NULL DEFAULT 0,

    /* Audit fields */
    created_date timestamp not null DEFAULT current_timestamp,
    created_by  UUID,
    last_modified_date timestamp not NULL DEFAULT current_timestamp,
    last_modified_by UUID,

    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES user_entity(id),
    CONSTRAINT fk_last_modified_by FOREIGN KEY (last_modified_by) REFERENCES user_entity(id)
);

create UNIQUE INDEX idx_unique_email on user_entity (upper(email));

GRANT SELECT, INSERT, UPDATE on user_entity to ${appUser};