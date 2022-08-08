-- Création de la table dataset_request dans projekt_data
CREATE TABLE projekt_data.new_dataset_request
(
    id           bigserial   NOT NULL,
    uuid         uuid        NOT NULL,
    title        varchar(150) NOT NULL,
    description  varchar(255) NOT NULL,
    project_fk int8,
    PRIMARY KEY (id),
    CONSTRAINT fk_project
        FOREIGN KEY(project_fk)
            REFERENCES projekt_data.project(id)
);
