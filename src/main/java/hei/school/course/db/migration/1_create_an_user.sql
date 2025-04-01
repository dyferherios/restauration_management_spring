	-- Step 1: Drop the user if it already exists
drop user IF EXISTS dyferherios;

-- Step 2: Create the user with the desired password
create user dyferherios with ENCRYPTED PASSWORD System.getenv("DB_USER");
    
\du --verify role

-- Step 3: Drop the database if it exists (optional, if recreating the DB is needed)
drop database IF EXISTS restaurant_management;

-- Step 4: Create the database
create DATABASE restaurant_management;
grant connect on DATABASE restaurant_management TO dyferherios;
grant USAGE on SCHEMA public TO dyferherios;
grant
select, insert,
update, delete on ALL TABLES IN SCHEMA public TO dyferherios;
grant USAGE on ALL SEQUENCES IN SCHEMA public TO dyferherios;
grant REFERENCES on ALL TABLES IN SCHEMA public TO dyferherios;
alter DEFAULT PRIVILEGES IN SCHEMA public
grant
select, insert,
update, delete on TABLES to dyferherios;
GRANT USAGE, SELECT ON SEQUENCE stock_movement_id_seq TO dyferherios;


-- Find the process IDs of the sessions connected to the database:
-- SELECT pid, usename, application_name
-- FROM pg_stat_activity
-- WHERE datname = 'library_management';


-- Terminate the sessions using the pg_terminate_backend() function:
-- SELECT pg_terminate_backend(pid)
--FROM pg_stat_activity
--WHERE datname = 'library_management';
