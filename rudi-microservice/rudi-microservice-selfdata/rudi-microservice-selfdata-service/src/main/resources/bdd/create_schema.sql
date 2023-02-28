--------------------------------------------------------------------------
--
-- Creation de la base, du rôle et des schémas
--
--------------------------------------------------------------------------
CREATE USER selfdata WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  PASSWORD 'selfdata';

ALTER USER selfdata SET search_path TO selfdata_data, public;

CREATE SCHEMA selfdata_data AUTHORIZATION postgres;
GRANT ALL ON SCHEMA selfdata_data TO selfdata;




