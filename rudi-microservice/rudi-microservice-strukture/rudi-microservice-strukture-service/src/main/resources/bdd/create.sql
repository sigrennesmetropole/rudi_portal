--------------------------------------------------------------------------
--
-- Creation de la base, du rôle et des schémas
--
--------------------------------------------------------------------------

CREATE USER strukture WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION;

ALTER USER strukture SET search_path TO strukture_data, public;

CREATE SCHEMA strukture_data AUTHORIZATION postgres;
GRANT ALL ON SCHEMA strukture_data TO strukture;




