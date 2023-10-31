-- Creation de la table pour les nouveaux status de reutilisation
create table if not exists projekt_data.reutilisation_status (id  bigserial not null, uuid uuid not null, code varchar(30) not null, label varchar(100), closing_date timestamp, opening_date timestamp not null, order_ int4 not null, primary key (id));
alter table if exists projekt_data.reutilisation_status add constraint UK_i4rtjydn0ub0v5cefeyig03l5 unique (uuid);

-- Insert default value
INSERT INTO projekt_data.reutilisation_status(uuid, code, label, opening_date, order_)
VALUES ('eb6a094b-6090-40cc-a2a6-9d11c2be7aed', 'REUSE_FINISHED', 'Réutilisation - Finalisée', now(), 0);

-- Modification de la table project pour intégration de sa nouvelle colonne FK de la table crée précédemment
ALTER table if exists projekt_data.project
    ADD column if not exists reutilisation_status_fk int8;

-- Set old reuse status to FINISHED
UPDATE projekt_data.project
SET reutilisation_status_fk = 1
WHERE reutilisation_status_fk IS null;

-- Add not null constraint to reutilisation_status_fk
ALTER table if exists projekt_data.project
    ALTER COLUMN reutilisation_status_fk SET NOT NULL;

ALTER table if exists projekt_data.project
    ADD CONSTRAINT FKey2jx1ew3uvbl3huh62jspk8f FOREIGN KEY (reutilisation_status_fk) references projekt_data.reutilisation_status;