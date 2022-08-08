-- RUDI-1617 : Organisation
ALTER TABLE organization
    DROP CONSTRAINT organization_uk;
ALTER TABLE organization
    ADD COLUMN code varchar(30);
UPDATE organization
SET code = name;
ALTER TABLE organization
    ALTER COLUMN code SET NOT NULL;
ALTER TABLE organization
    RENAME COLUMN name TO label;
ALTER TABLE organization
    ADD COLUMN closing_date timestamp;
ALTER TABLE organization
    ADD COLUMN opening_date timestamp NOT NULL DEFAULT now();
ALTER TABLE organization
    ADD COLUMN order_ int4 NOT NULL DEFAULT 0;

-- RUDI-1618 : Membres d'une organisation
DROP TABLE strukture_data.organization_member;

-- Suppression de la migration Flyway
DELETE
FROM flyway_schema_history
WHERE version = '8';
