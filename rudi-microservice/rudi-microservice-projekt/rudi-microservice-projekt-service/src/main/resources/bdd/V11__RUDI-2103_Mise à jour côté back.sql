-- Ajouter la colonne date de fin à un linked_dataset
alter table projekt_data.linked_dataset add column end_date timestamp;

-- Supprimer la colonne "expected_completion_date"
alter table projekt_data.project drop column expected_completion_date;

-- Ajouter 2 colonnes période de fin
alter table projekt_data.project add column expected_completion_start_date timestamp;
alter table projekt_data.project add column expected_completion_end_date timestamp;
