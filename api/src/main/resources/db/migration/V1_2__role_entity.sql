CREATE TABLE authority_entity
(
    id   UUID        NOT NULL PRIMARY KEY,
    name VARCHAR(30) NOT NULL
);

CREATE UNIQUE INDEX idx_unique_authority ON authority_entity (UPPER(name));
GRANT SELECT ON authority_entity TO ${app_user};

/* ** Role entities ** */
CREATE TABLE role_entity
(
    id                 UUID        NOT NULL PRIMARY KEY,
    name               VARCHAR(60) NOT NULL,

    deleted           BOOLEAN     NOT NULL DEFAULT FALSE,

    /* OPT LOCK */
    version            INT         NOT NULL DEFAULT 0,

    /* Audit fields */
    created_date       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by         UUID        NOT NULL,
    last_modified_date TIMESTAMP,
    last_modified_by   UUID,

    CONSTRAINT fk_create_by FOREIGN KEY (created_by) REFERENCES user_entity (id),
    CONSTRAINT fk_modified_by FOREIGN KEY (last_modified_by) REFERENCES user_entity (id)
);

CREATE UNIQUE INDEX idx_unique_role ON role_entity (UPPER(name));
GRANT SELECT, INSERT, UPDATE ON role_entity TO ${app_user};

/* ** Role authority lookup table ** */
CREATE TABLE role_authority_lookup
(
    role_id      UUID NOT NULL,
    authority_id UUID NOT NULL,

    CONSTRAINT fk_role_entity FOREIGN KEY (role_id) REFERENCES role_entity (id),
    CONSTRAINT fk_authority_entity FOREIGN KEY (authority_id) REFERENCES authority_entity (id),
    PRIMARY KEY (role_id, authority_id)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON role_authority_lookup TO ${app_user};

/* ** User role lookup table ** */
CREATE TABLE user_role_lookup
(
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,

    CONSTRAINT fk_user_entity FOREIGN KEY (user_id) REFERENCES user_entity (id),
    CONSTRAINT fk_role_entity FOREIGN KEY (role_id) REFERENCES role_entity (id),
    PRIMARY KEY (user_id, role_id)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON user_role_lookup TO ${app_user};