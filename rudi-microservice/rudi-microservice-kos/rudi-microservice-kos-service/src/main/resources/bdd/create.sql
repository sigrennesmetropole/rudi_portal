--------------------------------------------------------------------------
--
-- Creation de la base, du rôle et des schémas
--
--------------------------------------------------------------------------
CREATE USER kos WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION;

ALTER USER kos SET search_path TO kos_data, public;

CREATE SCHEMA kos_data AUTHORIZATION postgres;
GRANT ALL ON SCHEMA kos_data TO kos;




