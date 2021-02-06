/*
    Sarah Conner
    Howard TheDuck
    Joe Exotic
    John Wick
*/

/* Sarah Conner */
INSERT INTO user_entity(id, email, password, first_name, last_name, created_date)
    VALUES ('21af2672-f165-44bd-9b35-0845934fbe84' , 'sarah@conner.com', 'password', 'Sarah', 'Conner', '2020-01-01');

INSERT INTO user_role_lookup(user_id, role_id)
    VALUES ((SELECT id FROM user_entity WHERE email = 'sarah@conner.com'),
            (SELECT id FROM role_entity WHERE name = 'USER_ROLE'));

/*  Howard the duck */
INSERT INTO user_entity(id, email, password, first_name, last_name, created_date)
    VALUES ('99c9c540-5f07-442e-9964-7da9e911f3a5', 'howard@theDuck.com', 'password', 'Howard', 'TheDuck', '2020-02-01');

INSERT INTO user_role_lookup(user_id, role_id)
    VALUES ((SELECT id FROM user_entity WHERE email = 'howard@theDuck.com'),
            (SELECT id FROM role_entity WHERE name = 'USER_ROLE'));

/* Joe Exotic */
INSERT INTO user_entity(id, email, password, first_name, last_name, created_date)
    VALUES ('773e3ac5-c0a1-4cdd-99a6-58c167cdd40a', 'joe@exotic.net', 'password', 'Joe', 'Exotic', '2020-03-01');

INSERT INTO user_role_lookup(user_id, role_id)
    VALUES ((SELECT id FROM user_entity WHERE email = 'joe@exotic.net'),
            (SELECT id FROM role_entity WHERE name = 'USER_ROLE'));

/* John Wick */
INSERT INTO user_entity(id, email, password, first_name, last_name, created_date)
    VALUES ('7e53e8a2-f215-4508-bde7-ead7d55a83e0', 'john@wick.com', 'password', 'John', 'Wick', '2020-04-01');

INSERT INTO user_role_lookup(user_id, role_id)
    VALUES ((SELECT id FROM user_entity WHERE email = 'john@wick.com'),
            (SELECT id FROM role_entity WHERE name = 'USER_ROLE'));
