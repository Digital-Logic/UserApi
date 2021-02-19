/* ** User Status BiTemporal entity ** */

CREATE TABLE user_status
(
    id                  UUID      NOT NULL,

    /* ** Temporal Properties ** */
    valid_start         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valid_stop          TIMESTAMP NOT NULL DEFAULT 'INFINITY'::TIMESTAMP,

    system_start        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    system_stop         TIMESTAMP NOT NULL DEFAULT 'INFINITY'::TIMESTAMP,
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,

    /* ** User Status Properties ** */
    account_enabled     BOOLEAN   NOT NULL DEFAULT FALSE,
    account_expired     BOOLEAN   NOT NULL DEFAULT FALSE,
    account_locked      BOOLEAN   NOT NULL DEFAULT FALSE,
    credentials_expired BOOLEAN   NOT NULL DEFAULT FALSE,

    /* ** Audit Properties ** */
    created_by          UUID      NOT NULL,

    audit_message       UUID,

    PRIMARY KEY (id, valid_start, system_start),

    CONSTRAINT fk_user_entity FOREIGN KEY (id) REFERENCES user_entity (id),
    CONSTRAINT fk_audit_message FOREIGN KEY (audit_message) REFERENCES audit_message (id)
);

CREATE INDEX idx_valid_time ON user_status (id, valid_start DESC, valid_stop);
CREATE INDEX idx_system_time ON user_status (id, system_start DESC, system_stop);

GRANT SELECT, INSERT, UPDATE ON user_status TO ${app_user};