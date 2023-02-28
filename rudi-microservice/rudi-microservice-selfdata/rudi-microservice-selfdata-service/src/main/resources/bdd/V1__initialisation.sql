CREATE TABLE IF NOT EXISTS selfdata_data.selfdata_information_request (
    process_definition_version              integer,
    description                             varchar(255)                                                                    NOT NULL,
    id                                      bigserial CONSTRAINT selfdata_information_request_pkey PRIMARY KEY,
    uuid                                    uuid                                                                            NOT NULL,
    selfdata_information_request_status     varchar(20)  DEFAULT 'DRAFT'::character varying                                 NOT NULL,
    process_definition_key                  varchar(150) DEFAULT 'selfdata-information-request-process'::character varying  NOT NULL,
    status                                  varchar(20)  DEFAULT 'DRAFT'::character varying                                 NOT NULL,
    functional_status                       varchar(50)  DEFAULT ''::character varying                                      NOT NULL,
    initiator                               varchar(50)  DEFAULT 'rudi'::character varying                                  NOT NULL,
    updator                                 varchar(50),
    creation_date                           timestamp    DEFAULT now()                                                      NOT NULL,
    updated_date                            timestamp    DEFAULT now()                                                      NOT NULL,
    assignee                                varchar(100),
    data                                    text,
    dataset_uuid                            uuid                                                                            NOT NULL
);

CREATE TABLE section_definition
(
    id bigserial not null,
    uuid uuid not null,
    definition text NOT NULL,
    label character varying(150),
    help character varying(150),
    name character varying(100)  NOT NULL,
    CONSTRAINT section_definition_pkey PRIMARY KEY (id)
);

CREATE TABLE form_definition
(
    id bigserial not null,
    uuid uuid not null,
    name character varying(100)  NOT NULL,
    CONSTRAINT form_definition_pkey PRIMARY KEY (id)
);

CREATE TABLE form_section_definition
(
    id bigserial not null,
    uuid uuid not null,
    order_ integer NOT NULL,
    read_only boolean NOT NULL,
    section_definition_fk bigint,
    form_definition_fk bigint,
    CONSTRAINT form_section_definition_pkey PRIMARY KEY (id),
    CONSTRAINT fkjpx0jm21mae9injyxgeaavfs FOREIGN KEY (section_definition_fk)
        REFERENCES section_definition (id),
    CONSTRAINT fkjvk89pj3kxxr22ir9gc4dc4b3 FOREIGN KEY (form_definition_fk)
        REFERENCES form_definition (id)
);

CREATE TABLE process_form_definition
(
    id bigserial not null,
    uuid uuid not null,
    process_definition_id character varying(64)  NOT NULL,
    revision integer,
    user_task_id character varying(64) ,
    action_name character varying(64) ,
    form_definition_fk bigint,
    CONSTRAINT process_form_definition_pkey PRIMARY KEY (id),
    CONSTRAINT fka46uc2xkhmviwxnbbgudfi3n3 FOREIGN KEY (form_definition_fk)
        REFERENCES form_definition (id)
);
