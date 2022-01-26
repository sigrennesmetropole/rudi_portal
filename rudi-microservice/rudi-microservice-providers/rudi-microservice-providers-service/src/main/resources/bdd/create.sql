--------------------------------------------------------------------------
--
-- Creation de la base, du rôle et des schémas
--
--------------------------------------------------------------------------

CREATE USER providers WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION;

ALTER USER providers SET search_path TO providers_data, public;

CREATE SCHEMA providers_data AUTHORIZATION postgres;
GRANT ALL ON SCHEMA providers_data TO providers;




