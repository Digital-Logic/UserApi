CREATE TABLE verification_token
(
    id                 VARCHAR(80) NOT NULL PRIMARY KEY,
    token_type         VARCHAR(30) NOT NULL,
    user_id            UUID        NOT NULL,

    expires            TIMESTAMP   NOT NULL,

    /* OPT LOCK */
    version            INT         NOT NULL DEFAULT 0,

    /* ** Audit fields ** */
    created_date       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP,

    CONSTRAINT fk_user_entity FOREIGN KEY (user_id) REFERENCES user_entity (id)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON verification_token TO ${app_user};