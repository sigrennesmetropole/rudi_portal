--------------------------------------------------------------------------
--
-- Creation de la base, du rôle et des schémas
--
--------------------------------------------------------------------------
CREATE USER kalim WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION;

ALTER USER kalim SET search_path TO kalim_data, public;

CREATE SCHEMA kalim_data AUTHORIZATION postgres;
GRANT ALL ON SCHEMA kalim_data TO kalim;




