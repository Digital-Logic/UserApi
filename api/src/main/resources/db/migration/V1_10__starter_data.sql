INSERT INTO user_entity (id, email, password, first_name, last_name)
    VALUES (uuid_generate_v4(), ${sysAccountEmail}, ${sysAccountPwd}, 'System', 'Account');