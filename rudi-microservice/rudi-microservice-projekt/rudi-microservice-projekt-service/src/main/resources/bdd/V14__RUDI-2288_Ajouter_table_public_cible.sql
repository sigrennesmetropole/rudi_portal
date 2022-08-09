-- Création de la table target_audience dans projekt_data
create table projekt_data.target_audience
(
    id           bigserial   not null,
    uuid         uuid        not null,
    code         varchar(30) not null,
    label        varchar(100),
    opening_date timestamp   not null,
    closing_date timestamp,
    order_       int4        not null,
    primary key (id)
);

-- Suppression de l'ancienne table project_audience
DROP TABLE IF EXISTS projekt_data.project_audience;

-- Creation de la table de relation
create table projekt_data.project_audience (project_fk int8 not null, target_audience_fk int8 not null, primary key (project_fk, target_audience_fk));

-- Ajout des contraintes de foreign key
alter table if exists projekt_data.project_audience add constraint FK6qce4h6cmt0nan0wbw6i50dpb foreign key (target_audience_fk) references projekt_data.target_audience;
alter table if exists projekt_data.project_audience add constraint FKrlkggrowlus4fdwr2am22qr8n foreign key (project_fk) references projekt_data.project;