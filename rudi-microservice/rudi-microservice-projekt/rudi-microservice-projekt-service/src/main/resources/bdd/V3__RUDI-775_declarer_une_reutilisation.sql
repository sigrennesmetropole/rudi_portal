-- Ajout de la colonne access_url
alter table projekt_data.project
    add column access_url varchar(255);

-- Ajout de la colonne contact_email
alter table projekt_data.project
    add column contact_email varchar(100);
update projekt_data.project
set contact_email = 'rudi@rennes-metropole.fr';
alter table projekt_data.project
    ALTER column contact_email SET not null;

-- Ajout de la table de liaison projet <-> JDD
create table projekt_data.project_dataset
(
    project_fk   int8 not null,
    dataset_uuid uuid
);
alter table if exists projekt_data.project_dataset
    add constraint FKl93nymvl8q2pvdxrjjxq2j5bl foreign key (project_fk) references projekt_data.project;

-- Ajout d'un type d'accompagnement par défaut
INSERT INTO projekt_data.support(uuid, code, label, opening_date, order_)
VALUES ('116ad687-1935-4eeb-85a0-5e262a94dcbc', 'AUCUN', 'Aucun', now(), COALESCE((select max(order_) from projekt_data.support), 0) + 1);
