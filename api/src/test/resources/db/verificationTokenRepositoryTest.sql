INSERT INTO user_entity(id, email, password, first_name, last_name)
VALUES ('4876a5ba-319e-4ca1-849d-1f6cb5e3524c', 'howardTheDuck@gmail.com',
        '{bcrypt}$2a$10$ZiSDjl9BEom0FhqR9uQ8SOXCCfyN2MhbiLC6JTSkNnK/alLWeE2M2', 'Howard', 'TheDuck');

INSERT INTO user_role_lookup(user_id, role_id)
VALUES ('4876a5ba-319e-4ca1-849d-1f6cb5e3524c',
        (SELECT id FROM role_entity WHERE name = 'ADMIN_ROLE'));

INSERT INTO user_status(id, valid_start, system_start, system_stop, created_by)
VALUES ('4876a5ba-319e-4ca1-849d-1f6cb5e3524c',
        NOW() - INTERVAL '30 days',
        NOW() - INTERVAL '30 days',
        NOW() - INTERVAL '25 days',
        '4876a5ba-319e-4ca1-829d-1f6cb5e3599f');

insert into verification_token (id, token_type, user_id, expires)
values ('fJvhX9z1L+c4QN3MD8ZFWdNamUnK7JIQQyicDzMIevk+CTQG', 1, '4876a5ba-319e-4ca1-849d-1f6cb5e3524c', current_timestamp + interval '1h' * 3);