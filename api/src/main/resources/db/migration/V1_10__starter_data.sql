INSERT INTO user_entity (id, email, password, first_name, last_name)
    VALUES (uuid_generate_v4(), ${sysAccountEmail}, ${sysAccountPwd}, 'System', 'Account');

INSERT INTO user_status(id, account_locked, created_by)
    (select id, true, id from user_entity where email = ${sysAccountEmail});

/* Authorities */
INSERT INTO authority_entity (id, name)
    VALUES (uuid_generate_v4(), 'ADMIN_USERS'),
           (uuid_generate_v4(), 'ADMIN_ROLES');

/* ** Role_Entity Insert ** */
INSERT INTO role_entity (id, name, created_by)
    VALUES (uuid_generate_v4(), 'ADMIN_ROLE', (SELECT id FROM user_entity WHERE email = ${sysAccountEmail})),
           (uuid_generate_v4(), 'USER_ROLE', (SELECT id FROM user_entity WHERE email = ${sysAccountEmail}));


/* ** Role_Authority Lookup table insert ** */
INSERT INTO role_authority_lookup(role_id, authority_id)
    VALUES ((SELECT id FROM role_entity WHERE name = 'ADMIN_ROLE'),
            (SELECT id FROM authority_entity WHERE name = 'ADMIN_USERS')),

           ((SELECT id FROM role_entity WHERE name = 'ADMIN_ROLE'),
            (SELECT id FROM authority_entity WHERE name = 'ADMIN_ROLES'));