INSERT INTO user_entity(id, email, password, first_name, last_name) VALUES
    (uuid_generate_v4(), 'test@testing.com', 'password', 'John', 'Doe');

INSERT INTO user_role_lookup(user_id, role_id) VALUES
((select id from user_entity where email = 'test@testing.com'),
 (select id from role_entity where name = 'USER_ROLE'));