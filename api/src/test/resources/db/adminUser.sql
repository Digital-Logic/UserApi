INSERT INTO user_entity(id, email, password, first_name, last_name)
    VALUES (uuid_generate_v4(), 'adminTestUser@gmail.com', 'password', 'John', 'Doe');

INSERT INTO user_role_lookup(user_id, role_id)
    VALUES ((SELECT id FROM user_entity WHERE email = 'adminTestUser@gmail.com'),
            (SELECT id FROM role_entity WHERE name = 'ADMIN_ROLE'));