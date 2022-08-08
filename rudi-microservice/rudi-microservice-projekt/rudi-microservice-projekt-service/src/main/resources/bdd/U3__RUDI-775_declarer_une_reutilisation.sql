-- Suppression de la colonne access_url
alter table projekt_data.project
    drop column access_url;

-- Suppression de la colonne contact_email
alter table projekt_data.project
    drop column contact_email;

-- Suppression de la table de liaison projet <-> JDD
drop table projekt_data.project_dataset;

-- Suppression du type d'accompagnement par défaut
delete
from projekt_data.project_support
where support_fk = (select id from projekt_data.support where uuid = '116ad687-1935-4eeb-85a0-5e262a94dcbc');
delete
from projekt_data.support
where uuid = '116ad687-1935-4eeb-85a0-5e262a94dcbc';

-- Suppression de la migration flyway
delete
from projekt_data.flyway_schema_history
where version = '3';
