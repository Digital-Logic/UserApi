SET TIME ZONE 'UTC';

CREATE TABLE user_entity
(
    id                 UUID         NOT NULL PRIMARY KEY,
    email              VARCHAR(80)  NOT NULL,
    password           VARCHAR(160) NOT NULL,
    first_name         VARCHAR(40)  NOT NULL,
    last_name          VARCHAR(40)  NOT NULL,
    archived           BOOLEAN      NOT NULL DEFAULT FALSE,

    /* OPT LOCK */
    version            INT          NOT NULL DEFAULT 0,

    /* ** Audit fields ** */
    created_date       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by         UUID,
    last_modified_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by   UUID,

    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES user_entity (id),
    CONSTRAINT fk_last_modified_by FOREIGN KEY (last_modified_by) REFERENCES user_entity (id)
);

CREATE UNIQUE INDEX idx_unique_email ON user_entity (UPPER(email));

GRANT SELECT, INSERT, UPDATE ON user_entity TO ${app_user};



