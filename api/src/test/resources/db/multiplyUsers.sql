/*
    Sarah Conner
    Howard TheDuck
    Joe Exotic
    John Wick
*/

/* Sarah Conner */
INSERT INTO user_entity(id, email, password, first_name, last_name)
    VALUES (uuid_generate_v4(), 'sarah@conner.com', 'password', 'Sarah', 'Conner');

INSERT INTO user_role_lookup(user_id, role_id)
    VALUES ((SELECT id FROM user_entity WHERE email = 'sarah@conner.com'),
            (SELECT id FROM role_entity WHERE name = 'USER_ROLE'));

/*  Howard the duck */
INSERT INTO user_entity(id, email, password, first_name, last_name)
    VALUES (uuid_generate_v4(), 'howard@theDuck.com', 'password', 'Howard', 'TheDuck');

INSERT INTO user_role_lookup(user_id, role_id)
    VALUES ((SELECT id FROM user_entity WHERE email = 'howard@theDuck.com'),
            (SELECT id FROM role_entity WHERE name = 'USER_ROLE'));

/* Joe Exotic */
INSERT INTO user_entity(id, email, password, first_name, last_name)
    VALUES (uuid_generate_v4(), 'joe@exotic.net', 'password', 'Joe', 'Exotic');

INSERT INTO user_role_lookup(user_id, role_id)
    VALUES ((SELECT id FROM user_entity WHERE email = 'joe@exotic.net'),
            (SELECT id FROM role_entity WHERE name = 'USER_ROLE'));

/* John Wick */
INSERT INTO user_entity(id, email, password, first_name, last_name)
    VALUES (uuid_generate_v4(), 'john@wick.com', 'password', 'John', 'Wick');

INSERT INTO user_role_lookup(user_id, role_id)
    VALUES ((SELECT id FROM user_entity WHERE email = 'john@wick.com'),
            (SELECT id FROM role_entity WHERE name = 'USER_ROLE'));
