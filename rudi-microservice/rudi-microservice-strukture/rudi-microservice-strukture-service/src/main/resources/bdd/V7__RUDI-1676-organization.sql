-- Création des tables précédemment dans projekt_data
CREATE TABLE organization
(
    id           bigserial   NOT NULL,
    uuid         uuid        NOT NULL,
    code         varchar(30) NOT NULL,
    label        varchar(100),
    closing_date timestamp,
    opening_date timestamp   NOT NULL,
    order_       int4        NOT NULL,
    PRIMARY KEY (id)
);
