-- Création de la table dataset_request dans projekt_data
CREATE TABLE projekt_data.linked_dataset
(
    id           bigserial   NOT NULL,
    uuid         uuid        NOT NULL,
    comment        varchar(150),
    dataset_uuid uuid NOT NULL,
    linked_dataset_status  varchar(20) NOT NULL,
    dataset_confidentiality  varchar(20) NOT NULL,
    project_fk int8,
    PRIMARY KEY (id),
    CONSTRAINT fk_project_1523
        FOREIGN KEY(project_fk)
            REFERENCES projekt_data.project(id)
);