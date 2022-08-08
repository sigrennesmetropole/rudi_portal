-- Ajouter la colonne dataset_organization_uuid à un linked_dataset
alter table projekt_data.linked_dataset add column dataset_organization_uuid uuid;
