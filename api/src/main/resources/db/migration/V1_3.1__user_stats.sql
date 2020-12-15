/* ** User Status BiTemporal entity ** */

CREATE TABLE user_status_entity
(
    id                  UUID      NOT NULL,

    /* ** Temporal Properties ** */
    valid_start         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valid_stop          TIMESTAMP NOT NULL DEFAULT 'INFINITY'::TIMESTAMP,

    system_start        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    system_stop         TIMESTAMP NOT NULL DEFAULT 'INFINITY'::TIMESTAMP,

    /* ** User Status Properties ** */
    enabled             BOOLEAN   NOT NULL DEFAULT FALSE,
    expired             BOOLEAN   NOT NULL DEFAULT FALSE,
    locked              BOOLEAN   NOT NULL DEFAULT FALSE,
    credentials_expired BOOLEAN   NOT NULL DEFAULT FALSE,

    /* ** Audit Properties ** */
    created_by          UUID      NOT NULL,
    created_date        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    audit_message       UUID,

    PRIMARY KEY (id, valid_start, system_start),

    CONSTRAINT fk_user_entity FOREIGN KEY (id) REFERENCES user_entity (id),
    CONSTRAINT fk_audit_message FOREIGN KEY (audit_message) REFERENCES audit_message_entity (id)
);

CREATE INDEX idx_valid_time ON user_status_entity (id, valid_start DESC, valid_stop);
CREATE INDEX idx_system_time ON user_status_entity (id, system_start DESC, system_stop);

GRANT SELECT, INSERT, UPDATE on user_status_entity to ${app_user};