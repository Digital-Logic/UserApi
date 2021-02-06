INSERT INTO user_entity(id, email, password, first_name, last_name, created_date)
    VALUES ('4718e879-c061-47bf-bcb4-a2db495b2fe9', 'test@testing.com', '{bcrypt}$2a$10$Vwszsopqc.4RCDi6.mHPleOek9dvb2S2ecwVAB.k1mCHu5p9nFf9i', 'John', 'Doe', '2020-01-01');

INSERT INTO user_role_lookup(user_id, role_id)
    VALUES ('4718e879-c061-47bf-bcb4-a2db495b2fe9', (SELECT id FROM role_entity WHERE name = 'USER_ROLE'));

INSERT INTO user_status(id, valid_start, system_start, system_stop, created_by)
    values ('4718e879-c061-47bf-bcb4-a2db495b2fe9', '2020-01-01','2020-01-01','2020-01-05', '4718e879-c061-47bf-bcb4-a2db495b2fe9');

INSERT INTO user_status(id, valid_start, valid_stop, system_start, account_enabled, created_by)
    values ('4718e879-c061-47bf-bcb4-a2db495b2fe9', '2020-01-01', '2020-01-05', '2020-01-05', false, '4718e879-c061-47bf-bcb4-a2db495b2fe9'),
           ('4718e879-c061-47bf-bcb4-a2db495b2fe9', '2020-01-05', 'infinity'::timestamp, '2020-01-05', true, '4718e879-c061-47bf-bcb4-a2db495b2fe9');