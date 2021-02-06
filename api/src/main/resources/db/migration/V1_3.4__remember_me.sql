CREATE TABLE persistent_logins
(
    username  VARCHAR(80) NOT NULL,
    series    VARCHAR(64) PRIMARY KEY,
    token     VARCHAR(64) NOT NULL,
    last_used TIMESTAMP   NOT NULL
);

GRANT SELECT, INSERT, UPDATE, DELETE on persistent_logins to ${app_user};