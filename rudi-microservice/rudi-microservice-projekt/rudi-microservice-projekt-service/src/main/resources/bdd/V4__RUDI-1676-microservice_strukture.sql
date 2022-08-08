-- address.EmailAddressEntity est supprimée
DROP TABLE email_address;

-- address.PostalAddressEntity est supprimée
DROP TABLE postal_address;

-- address.TelephoneAddressEntity est supprimée
DROP TABLE telephone_address;

-- address.AbstractAddressEntity est supprimée
DROP TABLE abstract_address;

-- address.AddressRoleEntity est supprimée
DROP TABLE address_role;

-- Pour suppression OrganizationEntity et ManagerEntity
DROP TABLE organization_manager;

-- ManagerEntity est déplacée dans le microservice strukture
ALTER TABLE project
    ADD COLUMN owner_uuid uuid;
UPDATE project
SET owner_uuid = (SELECT user_uuid FROM manager WHERE manager.id = project.manager_fk);
-- Les 3 requêtes suivantes doivent être séparées sinon H2 ne les accepte pas
ALTER TABLE project
    ALTER COLUMN owner_uuid SET NOT NULL;
ALTER TABLE project
    DROP CONSTRAINT fk16iqcnnmrl9xskpims6nw8pc1;
ALTER TABLE project
    DROP COLUMN manager_fk;
DROP TABLE manager;

-- OrganizationEntity est déplacée dans le microservice strukture
-- Les 3 requêtes suivantes doivent être séparées sinon H2 ne les accepte pas
ALTER TABLE project
    DROP CONSTRAINT fkjjm93q3pxup45kqa1j670x8n4;
ALTER TABLE project
    DROP COLUMN organization_fk;
DROP TABLE organization;
