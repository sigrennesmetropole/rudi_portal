--------------------------------------------------------------------------
--
-- Creation de la base, du rôle et des schémas
--
--------------------------------------------------------------------------
CREATE USER apigateway WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  PASSWORD 'apigateway';

ALTER USER apigateway SET search_path TO apigateway_data, public;

CREATE SCHEMA apigateway_data AUTHORIZATION postgres;
GRANT ALL ON SCHEMA apigateway_data TO apigateway;




