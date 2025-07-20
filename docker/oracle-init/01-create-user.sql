-- Create the seat_booking user and grant necessary privileges
ALTER SESSION SET "_ORACLE_SCRIPT"=true;

-- Create user
CREATE USER seat_booking IDENTIFIED BY seat_booking;

-- Grant necessary privileges
GRANT CONNECT, RESOURCE TO seat_booking;
GRANT CREATE SESSION TO seat_booking;
GRANT CREATE TABLE TO seat_booking;
GRANT CREATE SEQUENCE TO seat_booking;
GRANT CREATE VIEW TO seat_booking;
GRANT CREATE PROCEDURE TO seat_booking;
GRANT CREATE TRIGGER TO seat_booking;
GRANT CREATE TYPE TO seat_booking;
GRANT CREATE SYNONYM TO seat_booking;
GRANT CREATE ANY INDEX TO seat_booking;
GRANT ALTER ANY TABLE TO seat_booking;
GRANT DROP ANY TABLE TO seat_booking;

-- Grant unlimited tablespace quota
ALTER USER seat_booking QUOTA UNLIMITED ON USERS;

-- Grant DBA privileges (for development/testing purposes)
GRANT DBA TO seat_booking;

-- Connect as the new user and set default tablespace
CONNECT seat_booking/seat_booking@XE;
ALTER USER seat_booking DEFAULT TABLESPACE USERS;

-- Exit
EXIT;