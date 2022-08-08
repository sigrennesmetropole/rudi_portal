--------------------------------------------------------------------------
--
-- Creation de la base, du rôle et des schémas
--
--------------------------------------------------------------------------
CREATE USER projekt WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  PASSWORD 'projekt';

ALTER USER projekt SET search_path TO projekt_data, public;

CREATE SCHEMA projekt_data AUTHORIZATION postgres;
GRANT ALL ON SCHEMA projekt_data TO projekt;




