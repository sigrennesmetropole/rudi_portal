-- Modification de la table des membres des organisations
ALTER TABLE strukture_data.organization_member
    ADD COLUMN added_date timestamp NOT NULL
        DEFAULT '01/01/2023';