create user pm_migration_user with PASSWORD 'password';
GRANT CONNECT on DATABASE project_manager to pm_migration_user;
GRANT USAGE on SCHEMA public to pm_migration_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO pm_migration_user;

create user pm_app_user with PASSWORD 'password';
GRANT CONNECT on DATABASE project_manager to pm_app_user;
