-- TODO: Rendre consent_hash nullable
CREATE TABLE konsent_data.consent
(
    id                   bigserial    NOT NULL,
    uuid                 uuid         NOT NULL,
    consent_date         timestamp    NOT NULL,
    consent_hash         varchar(512),
    expiration_date      timestamp,
    owner_type           varchar(255) NOT NULL,
    owner_uuid           uuid NOT NULL,
    revoke_hash          varchar(512),
    storage_key          varchar(1024) NOT NULL,
    treatment_fk         int8,
    treatment_version_fk int8,
    PRIMARY KEY (id)
);
CREATE TABLE konsent_data.data_manager
(
    id           bigserial    NOT NULL,
    uuid         uuid         NOT NULL,
    address_1    varchar(150),
    address_2    varchar(150),
    email        varchar(150) NOT NULL,
    name         varchar(150) NOT NULL,
    phone_number varchar(150),
    PRIMARY KEY (id)
);
CREATE TABLE konsent_data.data_recipient
(
    id           bigserial   NOT NULL,
    uuid         uuid        NOT NULL,
    code         varchar(30) NOT NULL,
    closing_date timestamp,
    opening_date timestamp   NOT NULL,
    order_       int4        NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE konsent_data.dictionary_entry
(
    id   bigserial NOT NULL,
    uuid uuid      NOT NULL,
    lang varchar(10),
    text varchar(255),
    PRIMARY KEY (id)
);
CREATE TABLE konsent_data.involved_population_category
(
    id           bigserial   NOT NULL,
    uuid         uuid        NOT NULL,
    code         varchar(30) NOT NULL,
    closing_date timestamp,
    opening_date timestamp   NOT NULL,
    order_       int4        NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE konsent_data.purpose
(
    id           bigserial   NOT NULL,
    uuid         uuid        NOT NULL,
    code         varchar(30) NOT NULL,
    closing_date timestamp,
    opening_date timestamp   NOT NULL,
    order_       int4        NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE konsent_data.retention
(
    id           bigserial    NOT NULL,
    uuid         uuid         NOT NULL,
    code         varchar(30)  NOT NULL,
    closing_date timestamp,
    opening_date timestamp    NOT NULL,
    order_       int4         NOT NULL,
    unit         varchar(255) NOT NULL,
    value_        int4         NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE konsent_data.security_measure
(
    id           bigserial   NOT NULL,
    uuid         uuid        NOT NULL,
    code         varchar(30) NOT NULL,
    closing_date timestamp,
    opening_date timestamp   NOT NULL,
    order_       int4        NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE konsent_data.treatment
(
    id            bigserial    NOT NULL,
    uuid          uuid         NOT NULL,
    creation_date timestamp    NOT NULL,
    name          varchar(255) NOT NULL,
    owner_type    varchar(255) NOT NULL,
    owner_uuid    uuid         NOT NULL,
    status        varchar(255) NOT NULL,
    target_type   varchar(255) NOT NULL,
    target_uuid   uuid         NOT NULL,
    updated_date  timestamp,
    PRIMARY KEY (id)
);
CREATE TABLE konsent_data.treatment_version
(
    id                              bigserial    NOT NULL,
    uuid                            uuid         NOT NULL,
    creation_date                   timestamp    NOT NULL,
    data_recipient_detail           varchar(255),
    obsolete_date                   timestamp,
    outside_ue_transfert            varchar(255),
    security_measure_detail         varchar(255),
    status                          varchar(255) NOT NULL,
    updated_date                    timestamp,
    version                         int4         NOT NULL,
    treatment_hash			        varchar(512),
    data_protection_officer         int8,
    involved_population_category_fk int8,
    data_manager_fk                 int8,
    purpose_fk                      int8,
    retention_fk                    int8,
    typology_treatment_fk           int8,
    treatement_fk                   int8,
    PRIMARY KEY (id)
);
CREATE TABLE konsent_data.treatment_version_data_recipient
(
    treatment_version_fk int8 NOT NULL,
    data_recipient_fk    int8 NOT NULL,
    PRIMARY KEY (treatment_version_fk, data_recipient_fk)
);
CREATE TABLE konsent_data.treatment_version_mutlilingual_data
(
    treatment_version_fk int8 NOT NULL,
    dictionary_entry_fk  int8 NOT NULL,
    PRIMARY KEY (treatment_version_fk, dictionary_entry_fk)
);
CREATE TABLE konsent_data.treatment_version_mutlilingual_operation_nature
(
    treatment_version_fk int8 NOT NULL,
    dictionary_entry_fk  int8 NOT NULL,
    PRIMARY KEY (treatment_version_fk, dictionary_entry_fk)
);
CREATE TABLE konsent_data.treatment_version_mutlilingual_title
(
    treatment_version_fk int8 NOT NULL,
    dictionary_entry_fk  int8 NOT NULL,
    PRIMARY KEY (treatment_version_fk, dictionary_entry_fk)
);
CREATE TABLE konsent_data.treatment_version_mutlilingual_usage
(
    treatment_version_fk int8 NOT NULL,
    dictionary_entry_fk  int8 NOT NULL,
    PRIMARY KEY (treatment_version_fk, dictionary_entry_fk)
);
CREATE TABLE konsent_data.treatment_version_security_measure
(
    treatment_version_fk int8 NOT NULL,
    security_measure_fk  int8 NOT NULL,
    PRIMARY KEY (treatment_version_fk, security_measure_fk)
);
CREATE TABLE konsent_data.typology_treatment
(
    id           bigserial   NOT NULL,
    uuid         uuid        NOT NULL,
    code         varchar(30) NOT NULL,
    closing_date timestamp,
    opening_date timestamp   NOT NULL,
    order_       int4        NOT NULL,
    PRIMARY KEY (id)
);
ALTER TABLE if EXISTS konsent_data.consent ADD CONSTRAINT UK_g35w5e2tbyy45p9778ggb9e42 UNIQUE (uuid);
ALTER TABLE if EXISTS konsent_data.data_manager ADD CONSTRAINT UK_q11jn601td994scuh1usga4so UNIQUE (uuid);
ALTER TABLE if EXISTS konsent_data.data_recipient ADD CONSTRAINT UK_rji9j3un25vq8211gc601pa0e UNIQUE (uuid);
ALTER TABLE if EXISTS konsent_data.dictionary_entry ADD CONSTRAINT UK_h9sdbev6iac2gy0nqm837fr7x UNIQUE (uuid);
ALTER TABLE if EXISTS konsent_data.involved_population_category ADD CONSTRAINT UK_te1syii2sldlug4ikx8xidpkf UNIQUE (uuid);
ALTER TABLE if EXISTS konsent_data.purpose ADD CONSTRAINT UK_f3rt176iktneouxmiu3hyoxsa UNIQUE (uuid);
ALTER TABLE if EXISTS konsent_data.retention ADD CONSTRAINT UK_d1hot81tg9th6puybwv18hyyi UNIQUE (uuid);
ALTER TABLE if EXISTS konsent_data.security_measure ADD CONSTRAINT UK_4l5lxqemixcsvwcof25qsa95s UNIQUE (uuid);
ALTER TABLE if EXISTS konsent_data.treatment ADD CONSTRAINT UK_pmrmvt4w6uf1220g4v17sllv3 UNIQUE (uuid);
ALTER TABLE if EXISTS konsent_data.treatment_version ADD CONSTRAINT UK_rvlniqc58cgfsqbxfb9k9qqr0 UNIQUE (uuid);
ALTER TABLE if EXISTS konsent_data.typology_treatment ADD CONSTRAINT UK_u9xne0jxn9aaj9667jvy7825 UNIQUE (uuid);
CREATE TABLE data_recipient_dictionary_entry
(
    data_recipient_fk   int8 NOT NULL,
    dictionary_entry_fk int8 NOT NULL,
    PRIMARY KEY (data_recipient_fk, dictionary_entry_fk)
);
CREATE TABLE involved_population_category_dictionary_entry
(
    involved_population_category_fk int8 NOT NULL,
    dictionary_entry_fk             int8 NOT NULL,
    PRIMARY KEY (involved_population_category_fk, dictionary_entry_fk)
);
CREATE TABLE purpose_dictionary_entry
(
    purpose_fk          int8 NOT NULL,
    dictionary_entry_fk int8 NOT NULL,
    PRIMARY KEY (purpose_fk, dictionary_entry_fk)
);
CREATE TABLE retention_dictionary_entry
(
    retention_fk        int8 NOT NULL,
    dictionary_entry_fk int8 NOT NULL,
    PRIMARY KEY (retention_fk, dictionary_entry_fk)
);
CREATE TABLE security_measure_dictionary_entry
(
    security_measure_fk int8 NOT NULL,
    dictionary_entry_fk int8 NOT NULL,
    PRIMARY KEY (security_measure_fk, dictionary_entry_fk)
);
CREATE TABLE typology_treatment_dictionary_entry
(
    typology_treatment_fk int8 NOT NULL,
    dictionary_entry_fk   int8 NOT NULL,
    PRIMARY KEY (typology_treatment_fk, dictionary_entry_fk)
);
ALTER TABLE if EXISTS konsent_data.consent ADD CONSTRAINT FK9ai749vptatqg4usrtr2th7io FOREIGN KEY (treatment_fk) REFERENCES konsent_data.treatment;
ALTER TABLE if EXISTS konsent_data.consent ADD CONSTRAINT FKa9pghquunv24or4dlvrx8j06v FOREIGN KEY (treatment_version_fk) REFERENCES konsent_data.treatment_version;
ALTER TABLE if EXISTS konsent_data.treatment_version ADD CONSTRAINT FKa9374kmky48jv2ly8oce8y5ht FOREIGN KEY (data_protection_officer) REFERENCES konsent_data.data_manager;
ALTER TABLE if EXISTS konsent_data.treatment_version ADD CONSTRAINT FKl6xrrokb8fh1erpf3dubqk95i FOREIGN KEY (involved_population_category_fk) REFERENCES konsent_data.involved_population_category;
ALTER TABLE if EXISTS konsent_data.treatment_version ADD CONSTRAINT FKaj8cf3kksow09ibrooxl7ie8n FOREIGN KEY (data_manager_fk) REFERENCES konsent_data.data_manager;
ALTER TABLE if EXISTS konsent_data.treatment_version ADD CONSTRAINT FKh4n2aefmmmf5nghrd0467d306 FOREIGN KEY (purpose_fk) REFERENCES konsent_data.purpose;
ALTER TABLE if EXISTS konsent_data.treatment_version ADD CONSTRAINT FK4nrvd5t04rnhcuv1mnc74vmm5 FOREIGN KEY (retention_fk) REFERENCES konsent_data.retention;
ALTER TABLE if EXISTS konsent_data.treatment_version ADD CONSTRAINT FK3tgrtaaa13rd1ji8xji5lnara FOREIGN KEY (typology_treatment_fk) REFERENCES konsent_data.typology_treatment;
ALTER TABLE if EXISTS konsent_data.treatment_version ADD CONSTRAINT FK2snhllhu73mvqvsl5icdxarrm FOREIGN KEY (treatement_fk) REFERENCES konsent_data.treatment;
ALTER TABLE if EXISTS konsent_data.treatment_version_data_recipient ADD CONSTRAINT FKi6cxc4eevw3cl25jsvf0wvb8s FOREIGN KEY (data_recipient_fk) REFERENCES konsent_data.data_recipient;
ALTER TABLE if EXISTS konsent_data.treatment_version_data_recipient ADD CONSTRAINT FKqx4kcblbeynmryfcmkjf5ixll FOREIGN KEY (treatment_version_fk) REFERENCES konsent_data.treatment_version;
ALTER TABLE if EXISTS konsent_data.treatment_version_mutlilingual_data ADD CONSTRAINT FKvpwjoy0fonvngsyht0e4qta3 FOREIGN KEY (dictionary_entry_fk) REFERENCES konsent_data.dictionary_entry;
ALTER TABLE if EXISTS konsent_data.treatment_version_mutlilingual_data ADD CONSTRAINT FKsqp2fxfc8w56qee4ey8dvbog FOREIGN KEY (treatment_version_fk) REFERENCES konsent_data.treatment_version;
ALTER TABLE if EXISTS konsent_data.treatment_version_mutlilingual_operation_nature ADD CONSTRAINT FKsc454kp5wxdcr44lrc334sofo FOREIGN KEY (dictionary_entry_fk) REFERENCES konsent_data.dictionary_entry;
ALTER TABLE if EXISTS konsent_data.treatment_version_mutlilingual_operation_nature ADD CONSTRAINT FKtl0anm1fi3v9iepjy1tyb2jip FOREIGN KEY (treatment_version_fk) REFERENCES konsent_data.treatment_version;
ALTER TABLE if EXISTS konsent_data.treatment_version_mutlilingual_title ADD CONSTRAINT FKi4n5qoenppg3xkajt0p1ltwbh FOREIGN KEY (dictionary_entry_fk) REFERENCES konsent_data.dictionary_entry;
ALTER TABLE if EXISTS konsent_data.treatment_version_mutlilingual_title ADD CONSTRAINT FKp4lpbd73joseib233xbobq1ms FOREIGN KEY (treatment_version_fk) REFERENCES konsent_data.treatment_version;
ALTER TABLE if EXISTS konsent_data.treatment_version_mutlilingual_usage ADD CONSTRAINT FK6fdkct9w6ah25t7qnidxx3dmu FOREIGN KEY (dictionary_entry_fk) REFERENCES konsent_data.dictionary_entry;
ALTER TABLE if EXISTS konsent_data.treatment_version_mutlilingual_usage ADD CONSTRAINT FK3p8rqy1c5r11xunox99c0sv2f FOREIGN KEY (treatment_version_fk) REFERENCES konsent_data.treatment_version;
ALTER TABLE if EXISTS konsent_data.treatment_version_security_measure ADD CONSTRAINT FKmglngkhb4ab99p1vjullmf85m FOREIGN KEY (security_measure_fk) REFERENCES konsent_data.security_measure;
ALTER TABLE if EXISTS konsent_data.treatment_version_security_measure ADD CONSTRAINT FKmbemh5sriev0vcajuxft0n5ji FOREIGN KEY (treatment_version_fk) REFERENCES konsent_data.treatment_version;
ALTER TABLE if EXISTS data_recipient_dictionary_entry ADD CONSTRAINT FKciisf7sskd3rb3yrxlorqvnie FOREIGN KEY (dictionary_entry_fk) REFERENCES konsent_data.dictionary_entry;
ALTER TABLE if EXISTS data_recipient_dictionary_entry ADD CONSTRAINT FKcjah2dhco18j6qxontn7cgx3d FOREIGN KEY (data_recipient_fk) REFERENCES konsent_data.data_recipient;
ALTER TABLE if EXISTS involved_population_category_dictionary_entry ADD CONSTRAINT FK727u1hxlqea34m6b20ilc393n FOREIGN KEY (dictionary_entry_fk) REFERENCES konsent_data.dictionary_entry;
ALTER TABLE if EXISTS involved_population_category_dictionary_entry ADD CONSTRAINT FKsch63l7eb5bwb1e640p45gxvo FOREIGN KEY (involved_population_category_fk) REFERENCES konsent_data.involved_population_category;
ALTER TABLE if EXISTS purpose_dictionary_entry ADD CONSTRAINT FKn7md86wx4hej9wc8e1ni8mgqd FOREIGN KEY (dictionary_entry_fk) REFERENCES konsent_data.dictionary_entry;
ALTER TABLE if EXISTS purpose_dictionary_entry ADD CONSTRAINT FK47076as7s6gx56ysfflgxc7dx FOREIGN KEY (purpose_fk) REFERENCES konsent_data.purpose;
ALTER TABLE if EXISTS retention_dictionary_entry ADD CONSTRAINT FKor78koc6vw73wg9q2vq50osx3 FOREIGN KEY (dictionary_entry_fk) REFERENCES konsent_data.dictionary_entry;
ALTER TABLE if EXISTS retention_dictionary_entry ADD CONSTRAINT FKiwxeuxkhb2i1h2ha5edxgetqx FOREIGN KEY (retention_fk) REFERENCES konsent_data.retention;
ALTER TABLE if EXISTS security_measure_dictionary_entry ADD CONSTRAINT FKeqbf4kmm39au2d2lt35exphv8 FOREIGN KEY (dictionary_entry_fk) REFERENCES konsent_data.dictionary_entry;
ALTER TABLE if EXISTS security_measure_dictionary_entry ADD CONSTRAINT FK4jwroxylquxtguaqw0d8xv52r FOREIGN KEY (security_measure_fk) REFERENCES konsent_data.security_measure;
ALTER TABLE if EXISTS typology_treatment_dictionary_entry ADD CONSTRAINT FK9eb36nsljfbkd3n0jmg925cdc FOREIGN KEY (dictionary_entry_fk) REFERENCES konsent_data.dictionary_entry;
ALTER TABLE if EXISTS typology_treatment_dictionary_entry ADD CONSTRAINT FKif4xl88qhtorh4o74goof8jda FOREIGN KEY (typology_treatment_fk) REFERENCES konsent_data.typology_treatment;

--
-- -- Script de création des listes et sous-objets d'un Treatment (Purpose, Retention, ...)
--

-- Labels pour InvolvedPopulationCategory
INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('d464dfd1-ac66-4375-8d25-a8604888bdb4', 'FR_FR', 'Salariés');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('f4a974c1-9b67-48e5-9041-4a4b94b1bbd0', 'FR_FR', 'Services internes');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('0136d2c4-1283-4207-8d49-3058629bd6ae', 'FR_FR', 'Clients');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('0b93ed0a-58d7-48b6-9f04-2956420e5d91', 'FR_FR', 'Fournisseurs');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('6a272243-b304-4478-9234-13a39210b206', 'FR_FR', 'Prestataires');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('00705ef1-6900-46e2-bbc7-1fb8631631d6', 'FR_FR', 'Prospects');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('3ad56b40-fbfd-4ddb-ba41-f2485561ac3f', 'FR_FR', 'Candidats');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('5b3c465c-96ad-4069-b020-ba883b62cfc0', 'FR_FR', 'Autres');

INSERT INTO konsent_data.involved_population_category (uuid, code, opening_date, closing_date, order_)
VALUES ('4032cd23-4ddf-44d2-837d-e08e2ab6a19f', 'SALARY', timestamp '2023-01-08 01:00:00', NULL, 1);
		
INSERT INTO konsent_data.involved_population_category (uuid, code, opening_date, closing_date, order_)
VALUES ('618df5df-3a11-4f5f-8c91-6f466f6bd717', 'INTERNAL_SERVICES', timestamp '2023-01-08 01:00:00', NULL, 2);		

INSERT INTO konsent_data.involved_population_category (uuid, code, opening_date, closing_date, order_)
VALUES ('1a5438da-2bfc-439e-b1cb-5b324aff5f55', 'CLIENTS', timestamp '2023-01-08 01:00:00', NULL, 3);	

INSERT INTO konsent_data.involved_population_category (uuid, code, opening_date, closing_date, order_)
VALUES ('1694c1dc-7b3d-4439-aad2-cccf683e253a', 'PROVIDERS', timestamp '2023-01-08 01:00:00', NULL, 4);	
			
INSERT INTO konsent_data.involved_population_category (uuid, code, opening_date, closing_date, order_)
VALUES ('8a75444c-5402-4700-b0a3-3b1e895ac5b5', 'SERVICE_PROVIDERS', timestamp '2023-01-08 01:00:00', NULL, 5);
		
INSERT INTO konsent_data.involved_population_category (uuid, code, opening_date, closing_date, order_)
VALUES ('3f4e6f9b-d520-49cd-ae1e-1c4edbce711b', 'PROSPECTS', timestamp '2023-01-08 01:00:00', NULL, 6);

INSERT INTO konsent_data.involved_population_category (uuid, code, opening_date, closing_date, order_)
VALUES ('7f986899-fffd-4e10-bd7e-e3b628f76564', 'CANDIDATES', timestamp '2023-01-08 01:00:00', NULL, 7);

INSERT INTO konsent_data.involved_population_category (uuid, code, opening_date, closing_date, order_)
VALUES ('6c3d5618-e134-48c7-9ce1-a3ff5fd1ff11', 'OTHERS', timestamp '2023-01-08 01:00:00', NULL, 8);
			
-- Alimentation table d'association
INSERT INTO konsent_data.involved_population_category_dictionary_entry (involved_population_category_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.involved_population_category WHERE code = 'SALARY'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Salariés'));
INSERT INTO konsent_data.involved_population_category_dictionary_entry(involved_population_category_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.involved_population_category WHERE code = 'INTERNAL_SERVICES'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Services internes'));
INSERT INTO konsent_data.involved_population_category_dictionary_entry(involved_population_category_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.involved_population_category WHERE code = 'CLIENTS'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Clients'));
INSERT INTO konsent_data.involved_population_category_dictionary_entry(involved_population_category_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.involved_population_category WHERE code = 'PROVIDERS'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Fournisseurs'));
INSERT INTO konsent_data.involved_population_category_dictionary_entry(involved_population_category_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.involved_population_category WHERE code = 'SERVICE_PROVIDERS'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Prestataires'));
INSERT INTO konsent_data.involved_population_category_dictionary_entry(involved_population_category_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.involved_population_category WHERE code = 'PROSPECTS'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Prospects'));
INSERT INTO konsent_data.involved_population_category_dictionary_entry(involved_population_category_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.involved_population_category WHERE code = 'CANDIDATES'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Candidats'));
INSERT INTO konsent_data.involved_population_category_dictionary_entry(involved_population_category_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.involved_population_category WHERE code = 'OTHERS'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Autres'));
--
-- Labels pour SecurityMeasure
INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('f6a0fa04-e115-4a35-bdb5-4d45826e952d', 'FR_FR', 'Mesure de traçabilité');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('d51fa906-5b4a-477a-a546-b76e901c862d', 'FR_FR', 'Mesure de protection des logiciels');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('12c0cce6-705f-425f-a3c6-3b2c26a09a7d', 'FR_FR', 'Sauvegarde des données');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('64186555-f67b-4a1a-afbe-0e277e815800', 'FR_FR', 'Chiffrement des données');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('82ef4f58-7a04-4172-ae30-e7d8332e5fc3', 'FR_FR', 'Contrôle d''accès des utilisateurs');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('46d53145-69f8-40c9-8ad9-3e7de994a5e4', 'FR_FR', 'Contrôle des sous-traitants');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('07a57043-54b7-4e2a-abf0-949ec0320cfc', 'FR_FR', 'Autres mesures (à préciser)');

--
INSERT INTO konsent_data.security_measure (uuid, code, opening_date, closing_date, order_)
VALUES ('34ca59de-0e3b-463b-b5c3-eb98a2cf7a0b', 'TRACABILITY', timestamp '2023-01-08 01:00:00', NULL, 1);

INSERT INTO konsent_data.security_measure (uuid, code, opening_date, closing_date, order_)
VALUES ('1b1961c1-4bf7-4430-8929-71000548c074', 'SOFTWARE_PROTECTION', timestamp '2023-01-08 01:00:00', NULL, 2);

INSERT INTO konsent_data.security_measure (uuid, code, opening_date, closing_date, order_)
VALUES ('cbaad91b-7a33-4d2f-9c93-662cd81892b8', 'DATA_SAVE', timestamp '2023-01-08 01:00:00', NULL, 3);

INSERT INTO konsent_data.security_measure (uuid, code, opening_date, closing_date, order_)
VALUES ('92577905-1659-43d8-9495-7ccdd47e5fe1', 'DATA_CIPHER', timestamp '2023-01-08 01:00:00', NULL, 4);

INSERT INTO konsent_data.security_measure (uuid, code, opening_date, closing_date, order_)
VALUES ('dcc40860-aaa1-4d39-8db8-2b46b76a4f5c', 'ACCESS_CONTROL', timestamp '2023-01-08 01:00:00', NULL, 5);

INSERT INTO konsent_data.security_measure (uuid, code, opening_date, closing_date, order_)
VALUES ('c30ad9d0-74c4-4c22-9d19-b73b08f25e55', 'SERVICE_PROVIDERS_CONTROL', timestamp '2023-01-08 01:00:00', NULL, 6);

INSERT INTO konsent_data.security_measure (uuid, code, opening_date, closing_date, order_)
VALUES ('f9dd861b-e7ef-4aa2-9312-e591d6d9ab83', 'OTHERS', timestamp '2023-01-08 01:00:00', NULL, 7);

-- Alimentation table d'association
INSERT INTO konsent_data.security_measure_dictionary_entry(security_measure_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.security_measure WHERE code = 'TRACABILITY'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Mesure de traçabilité'));
INSERT INTO konsent_data.security_measure_dictionary_entry(security_measure_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.security_measure WHERE code = 'SOFTWARE_PROTECTION'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Mesure de protection des logiciels'));
INSERT INTO konsent_data.security_measure_dictionary_entry(security_measure_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.security_measure WHERE code = 'DATA_SAVE'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Sauvegarde des données'));
INSERT INTO konsent_data.security_measure_dictionary_entry(security_measure_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.security_measure WHERE code = 'DATA_CIPHER'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Chiffrement des données'));
INSERT INTO konsent_data.security_measure_dictionary_entry(security_measure_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.security_measure WHERE code = 'ACCESS_CONTROL'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Contrôle d''accès des utilisateurs'));
INSERT INTO konsent_data.security_measure_dictionary_entry(security_measure_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.security_measure WHERE code = 'SERVICE_PROVIDERS_CONTROL'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Contrôle des sous-traitants'));
INSERT INTO konsent_data.security_measure_dictionary_entry(security_measure_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.security_measure WHERE code = 'OTHERS'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Autres mesures (à préciser)'));

--
-- Labels pour DataRecipient
INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('f7e6fe10-b5d1-4ee7-9011-7141629b33bb', 'FR_FR', 'Service interne qui traite les données');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('6c498d88-ca3b-45a4-bd49-a7d8485ceafb', 'FR_FR', 'Sous-traitants');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('1a484409-4e50-4b5e-b253-310a971a7817', 'FR_FR',
        'Destinataires dans des pays tiers ou organisations internationales');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('bf2a9b01-cba6-4525-9ac4-f2e1694f7bf8', 'FR_FR', 'Partenaires institutionnels ou commerciaux');

INSERT INTO konsent_data.dictionary_entry (uuid, lang, text)
VALUES ('13228b5b-017b-4d5b-b058-ae175a085cfb', 'FR_FR', 'Autre (Préciser)');

INSERT INTO konsent_data.data_recipient (uuid, code, opening_date, closing_date, order_)
VALUES ('2825b433-526d-41f1-b223-fdcc2b828672', 'INTERNAL_SERVICE', timestamp '2023-01-08 01:00:00', NULL, 1);

INSERT INTO konsent_data.data_recipient (uuid, code, opening_date, closing_date, order_)
VALUES ('b5b61972-83e7-4176-b9dd-decf08783498', 'SERVICE_PROVIDERS', timestamp '2023-01-08 01:00:00', NULL, 2);

INSERT INTO konsent_data.data_recipient (uuid, code, opening_date, closing_date, order_)
VALUES ('899aea53-7d11-433a-b9d7-532036877bd9', 'INTERNATIONAL', timestamp '2023-01-08 01:00:00', NULL, 3);

INSERT INTO konsent_data.data_recipient (uuid, code, opening_date, closing_date, order_)
VALUES ('a382997e-704a-4b05-a95c-6d14c1b9e39a', 'PARTNERS', timestamp '2023-01-08 01:00:00', NULL, 4);

INSERT INTO konsent_data.data_recipient (uuid, code, opening_date, closing_date, order_)
VALUES ('b5d85067-1db3-4288-804d-64385240e508', 'OTHERS', timestamp '2023-01-08 01:00:00', NULL, 5);

-- Alimentation table d'association
INSERT INTO konsent_data.data_recipient_dictionary_entry(data_recipient_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.data_recipient WHERE code = 'INTERNAL_SERVICE'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Service interne qui traite les données'));
INSERT INTO konsent_data.data_recipient_dictionary_entry(data_recipient_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.data_recipient WHERE code = 'SERVICE_PROVIDERS'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Sous-traitants'));
INSERT INTO konsent_data.data_recipient_dictionary_entry(data_recipient_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.data_recipient WHERE code = 'INTERNATIONAL'),
        (SELECT id
         FROM konsent_data.dictionary_entry
         WHERE text = 'Destinataires dans des pays tiers ou organisations internationales'));
INSERT INTO konsent_data.data_recipient_dictionary_entry(data_recipient_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.data_recipient WHERE code = 'PARTNERS'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Partenaires institutionnels ou commerciaux'));
INSERT INTO konsent_data.data_recipient_dictionary_entry(data_recipient_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.data_recipient WHERE code = 'OTHERS'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Autre (Préciser)'));

--
-- Labels pour Retention
INSERT INTO konsent_data.dictionary_entry(uuid, lang, text)
VALUES ('5b7ad33e-c109-4861-87aa-b5a7434cf529', 'FR_FR', 'Retention - Donnée temporaire');

INSERT INTO konsent_data.retention (uuid, code, opening_date, closing_date, order_, value_, unit)
VALUES ('4e00bbef-c08c-49ad-8c78-b01f210e219f', 'TEMPORARY', timestamp '2023-01-08 01:00:00', NULL, 1, 1,
        'YEAR');

-- Alimentation table d'association
INSERT INTO konsent_data.retention_dictionary_entry(retention_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.retention WHERE code = 'TEMPORARY'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Retention - Donnée temporaire'));

--
-- Labels pour Purpose
INSERT INTO konsent_data.dictionary_entry(uuid, lang, text)
VALUES ('ab9f06a2-a9d0-4c30-bced-59b2cff67816', 'FR_FR', 'Intérêt');

INSERT INTO konsent_data.purpose (uuid, code, opening_date, closing_date, order_)
VALUES ('cc8cef4b-d161-4692-9a28-c77b60198173', 'INTEREST', timestamp '2023-01-08 01:00:00', NULL, 1);

-- Alimentation table d'association
INSERT INTO konsent_data.purpose_dictionary_entry(purpose_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.purpose WHERE code = 'INTEREST'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Intérêt'));

--
-- Labels pour Typology
INSERT INTO konsent_data.dictionary_entry(uuid, lang, text)
VALUES ('7663ddaf-068a-4b81-bde3-38ae7e5c8597', 'FR_FR', 'Collecte');

INSERT INTO konsent_data.dictionary_entry(uuid, lang, text)
VALUES ('b0791860-0f3f-4238-aff0-d0411b30551f', 'FR_FR', 'Enregistrement organisation');

INSERT INTO konsent_data.dictionary_entry(uuid, lang, text)
VALUES ('481eb27d-c7d9-45d7-937d-77215deeff4e', 'FR_FR', 'Conservation');

INSERT INTO konsent_data.dictionary_entry(uuid, lang, text)
VALUES ('a0e1d385-cf17-4136-9b43-4260db083a8a', 'FR_FR', 'Adaptation');

INSERT INTO konsent_data.dictionary_entry(uuid, lang, text)
VALUES ('eea2a4b7-6c5c-44f1-bf69-710a4065ac0a', 'FR_FR', 'Modification');

INSERT INTO konsent_data.dictionary_entry(uuid, lang, text)
VALUES ('714bf114-8e99-40b9-a3d5-af8ffc9a2ad0', 'FR_FR', 'Extraction consultation');

INSERT INTO konsent_data.dictionary_entry(uuid, lang, text)
VALUES ('11b981ac-5681-4f62-b6fc-7527ae594fcd', 'FR_FR', 'Utilisation');

INSERT INTO konsent_data.dictionary_entry(uuid, lang, text)
VALUES ('27bc72dc-e1dd-4cde-876f-db0676c87ed0', 'FR_FR', 'Communication transmission');

INSERT INTO konsent_data.dictionary_entry(uuid, lang, text)
VALUES ('ad71a8d8-ef01-42e4-87c8-589b0142354b', 'FR_FR', 'Communication diffusion');

INSERT INTO konsent_data.typology_treatment (uuid, code, opening_date, closing_date, order_)
VALUES ('865e0b13-6925-4028-af29-3af5f734d151', 'COLLECTE', timestamp '2023-01-08 01:00:00', NULL, 1);

INSERT INTO konsent_data.typology_treatment (uuid, code, opening_date, closing_date, order_)
VALUES ('80f049fc-ca01-42b0-a878-0cbb8da32cf3', 'ORGANIZATION_REGISTRATION', timestamp '2023-01-08 01:00:00', NULL, 5);

INSERT INTO konsent_data.typology_treatment (uuid, code, opening_date, closing_date, order_)
VALUES ('eee06450-6c68-4795-bdc5-ceb19cb2a013', 'CONSERVATION', timestamp '2023-01-08 01:00:00', NULL, 10);

INSERT INTO konsent_data.typology_treatment (uuid, code, opening_date, closing_date, order_)
VALUES ('f45f85b1-d34b-40ba-8e78-ec1cc8f8eb68', 'ADAPTATION', timestamp '2023-01-08 01:00:00', NULL, 15);

INSERT INTO konsent_data.typology_treatment (uuid, code, opening_date, closing_date, order_)
VALUES ('8d966ad2-83c4-4f77-b20e-cb4f126fb43c', 'MODIFICATION', timestamp '2023-01-08 01:00:00', NULL, 20);

INSERT INTO konsent_data.typology_treatment (uuid, code, opening_date, closing_date, order_)
VALUES ('a5f3eba8-9677-4805-b7f6-c8ae51453704', 'STORAGE_CONSULTATION', timestamp '2023-01-08 01:00:00', NULL, 25);

INSERT INTO konsent_data.typology_treatment (uuid, code, opening_date, closing_date, order_)
VALUES ('160b3b59-0fa0-4b0f-8078-5fc4c3209103', 'USE', timestamp '2023-01-08 01:00:00', NULL, 30);

INSERT INTO konsent_data.typology_treatment (uuid, code, opening_date, closing_date, order_)
VALUES ('b35d814d-e2d1-4de0-80c6-1b7d3de4a3f5', 'TRANSMISSION', timestamp '2023-01-08 01:00:00', NULL, 35);

INSERT INTO konsent_data.typology_treatment (uuid, code, opening_date, closing_date, order_)
VALUES ('25471c9d-54c0-4d0d-9727-9f4b9e4b6122', 'DIFFUSION', timestamp '2023-01-08 01:00:00', NULL, 40);

-- Alimentation table d'association
INSERT INTO konsent_data.typology_treatment_dictionary_entry(typology_treatment_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.typology_treatment WHERE code = 'COLLECTE'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Collecte'));

INSERT INTO konsent_data.typology_treatment_dictionary_entry(typology_treatment_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.typology_treatment WHERE code = 'ORGANIZATION_REGISTRATION'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Enregistrement organisation'));

INSERT INTO konsent_data.typology_treatment_dictionary_entry(typology_treatment_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.typology_treatment WHERE code = 'CONSERVATION'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Conservation'));

INSERT INTO konsent_data.typology_treatment_dictionary_entry(typology_treatment_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.typology_treatment WHERE code = 'ADAPTATION'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Adaptation'));

INSERT INTO konsent_data.typology_treatment_dictionary_entry(typology_treatment_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.typology_treatment WHERE code = 'MODIFICATION'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Modification'));

INSERT INTO konsent_data.typology_treatment_dictionary_entry(typology_treatment_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.typology_treatment WHERE code = 'STORAGE_CONSULTATION'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Extraction consultation'));

INSERT INTO konsent_data.typology_treatment_dictionary_entry(typology_treatment_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.typology_treatment WHERE code = 'USE'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Utilisation'));

INSERT INTO konsent_data.typology_treatment_dictionary_entry(typology_treatment_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.typology_treatment WHERE code = 'TRANSMISSION'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Communication transmission'));

INSERT INTO konsent_data.typology_treatment_dictionary_entry(typology_treatment_fk, dictionary_entry_fk)
VALUES ((SELECT id FROM konsent_data.typology_treatment WHERE code = 'DIFFUSION'),
        (SELECT id FROM konsent_data.dictionary_entry WHERE text = 'Communication diffusion'));
