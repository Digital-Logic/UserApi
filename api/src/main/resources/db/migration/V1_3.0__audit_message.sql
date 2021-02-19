CREATE TABLE audit_message
(
    id                 UUID      NOT NULL PRIMARY KEY,
    message            TEXT      NOT NULL,
    deleted           BOOLEAN   NOT NULL DEFAULT FALSE,

    /* ** Opt Lock ** */
    version            INT       NOT NULL DEFAULT 0,

    /* ** Audit fields ** */
    created_date       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by         UUID      NOT NULL,
    last_modified_date TIMESTAMP,
    last_modified_by   UUID,

    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES user_entity (id),
    CONSTRAINT fk_last_modified_by FOREIGN KEY (last_modified_by) REFERENCES user_entity (id)
);

GRANT SELECT, INSERT, UPDATE on audit_message to ${app_user};