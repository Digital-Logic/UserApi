create user migration_user with PASSWORD 'password';
GRANT CONNECT on DATABASE app_db to migration_user;
GRANT USAGE on SCHEMA public to migration_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO migration_user;

create user app_user with PASSWORD 'password';
GRANT CONNECT on DATABASE app_db to app_user;
