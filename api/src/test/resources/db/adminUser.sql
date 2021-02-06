INSERT INTO user_entity(id, email, password, first_name, last_name)
    VALUES ('4876a5ba-319e-4ca1-829d-1f6cb5e3599f', 'adminTestUser@gmail.com',
            '{bcrypt}$2a$10$ZiSDjl9BEom0FhqR9uQ8SOXCCfyN2MhbiLC6JTSkNnK/alLWeE2M2', 'John', 'Doe');

INSERT INTO user_role_lookup(user_id, role_id)
    VALUES ('4876a5ba-319e-4ca1-829d-1f6cb5e3599f',
            (SELECT id FROM role_entity WHERE name = 'ADMIN_ROLE'));

INSERT INTO user_status(id, valid_start, system_start, system_stop, created_by)
    VALUES ('4876a5ba-319e-4ca1-829d-1f6cb5e3599f',
            NOW() - INTERVAL '30 days',
            NOW() - INTERVAL '30 days',
            NOW() - INTERVAL '25 days',
            '4876a5ba-319e-4ca1-829d-1f6cb5e3599f');

INSERT INTO user_status(id, valid_start, valid_stop, system_start, created_by)
    (SELECT id,
            valid_start,
            system_stop,
            system_stop,
            id
         FROM user_status
         WHERE id =
               (SELECT id FROM user_entity WHERE email = 'adminTestUser@gmail.com')
    );

INSERT INTO user_status(id, valid_start, system_start, account_enabled, created_by)
    (
        SELECT id,
               valid_stop,
               system_start,
               TRUE,
               id
            FROM user_status us
            WHERE id = (SELECT id FROM user_entity WHERE email = 'adminTestUser@gmail.com')
              AND us.system_start = (SELECT MAX(system_start) FROM user_status us1 WHERE us1.id = us.id)
    );