--------------------------------------------------------------------------
--
-- Creation de la base, du rôle et des schémas
--
--------------------------------------------------------------------------
CREATE USER konsult WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION;

ALTER USER konsult SET search_path TO konsult_data, public;

CREATE SCHEMA konsult_data AUTHORIZATION postgres;
GRANT ALL ON SCHEMA konsult_data TO konsult;




