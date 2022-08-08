--------------------------------------------------------------------------
--
-- Creation de la base, du rôle et des schémas
--
--------------------------------------------------------------------------
CREATE USER template WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  PASSWORD 'project';

ALTER USER template SET search_path TO template_data, public;

CREATE SCHEMA template_data AUTHORIZATION postgres;
GRANT ALL ON SCHEMA template_data TO template;




