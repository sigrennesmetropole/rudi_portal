--------------------------------------------------------------------------
--
-- Creation de la base, du rôle et des schémas
--
--------------------------------------------------------------------------
CREATE USER konsent WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  PASSWORD 'konsent';

ALTER USER konsent SET search_path TO konsent_data, public;

CREATE SCHEMA konsent_data AUTHORIZATION postgres;
GRANT ALL ON SCHEMA konsent_data TO konsent;




