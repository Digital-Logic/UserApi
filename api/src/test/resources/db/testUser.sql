INSERT INTO user_entity(id, email, password, first_name, last_name)
    VALUES (uuid_generate_v4(), 'test@testing.com', 'password', 'John', 'Doe');

INSERT INTO user_role_lookup(user_id, role_id)
    VALUES ((SELECT id FROM user_entity WHERE email = 'test@testing.com'),
            (SELECT id FROM role_entity WHERE name = 'USER_ROLE'));

INSERT INTO user_status(id, valid_start, system_start, system_stop, created_by)
    (SELECT id,
            NOW() - INTERVAL '30 days',
            NOW() - INTERVAL '30 days',
            NOW() - INTERVAL '25 days',
            id
         FROM user_entity
         WHERE email = 'test@testing.com');

INSERT INTO user_status(id, valid_start, valid_stop, system_start, created_by)
    (SELECT id,
            valid_start,
            system_stop,
            system_stop,
            id
         FROM user_status
         WHERE id =
               (SELECT id FROM user_entity WHERE email = 'test@testing.com')
    );

INSERT INTO user_status(id, valid_start, system_start, account_enabled, created_by)
    (SELECT id,
            valid_stop,
            system_start,
            TRUE,
            id
         FROM user_status us
         WHERE id = (SELECT id FROM user_entity WHERE email = 'test@testing.com')
           AND us.system_start = (SELECT MAX(system_start) FROM user_status us1 WHERE us1.id = us.id)
    );