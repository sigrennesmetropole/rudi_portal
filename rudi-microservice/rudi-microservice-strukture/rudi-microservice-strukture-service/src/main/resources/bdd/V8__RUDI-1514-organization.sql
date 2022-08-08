-- RUDI-1617 : Organisation
ALTER TABLE organization
    ADD CONSTRAINT organization_uk UNIQUE (uuid);
ALTER TABLE organization
    DROP COLUMN code;
ALTER TABLE organization
    RENAME COLUMN label TO name;
ALTER TABLE organization
    DROP COLUMN order_;

-- RUDI-1618 : Membres d'une organisation
CREATE TABLE organization_member
(
    organization_fk int8 NOT NULL,
    role            varchar(100),
    user_uuid       uuid
);
ALTER TABLE organization_member
    ADD CONSTRAINT organization_member_uk UNIQUE (organization_fk, role, user_uuid);
ALTER TABLE organization_member
    ADD CONSTRAINT organization_member_organization_fk FOREIGN KEY (organization_fk) REFERENCES organization;
