-- Modification de la table des organisations
ALTER TABLE strukture_data.organization ADD COLUMN description VARCHAR(800);
ALTER TABLE strukture_data.organization ADD COLUMN url VARCHAR(80);
