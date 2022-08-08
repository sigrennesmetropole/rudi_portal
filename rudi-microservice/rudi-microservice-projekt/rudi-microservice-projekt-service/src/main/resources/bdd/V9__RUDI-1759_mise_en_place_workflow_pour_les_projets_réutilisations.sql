-- ajout des éléments propres au workflow
alter table projekt_data.project add column process_definition_key varchar(150) not null default 'project-process';
alter table projekt_data.project add column process_definition_version int4;
alter table projekt_data.project rename column status to project_status;
alter table projekt_data.project add column status varchar(20) not null default 'DRAFT';
alter table projekt_data.project add column functional_status varchar(50) not null default '';
alter table projekt_data.project rename column created to creation_date;
alter table projekt_data.project rename column modified to updated_date;
alter table projekt_data.project add column initiator varchar(50) not null default 'rudi';
alter table projekt_data.project add column assignee varchar(100);
alter table projekt_data.project add column updator varchar(50);
alter table projekt_data.project add column data text;

alter table projekt_data.new_dataset_request add column new_dataset_request_status varchar(20) not null default 'DRAFT';
alter table projekt_data.new_dataset_request add column process_definition_key varchar(150) not null default 'new-dataset-request-process';
alter table projekt_data.new_dataset_request add column process_definition_version int4;
alter table projekt_data.new_dataset_request add column status varchar(20) not null default 'DRAFT';
alter table projekt_data.new_dataset_request add column functional_status varchar(50) not null default '';
alter table projekt_data.new_dataset_request add column initiator varchar(50) not null default 'rudi';
alter table projekt_data.new_dataset_request add column updator varchar(50);
alter table projekt_data.new_dataset_request add column creation_date timestamp not null default now();
alter table projekt_data.new_dataset_request add column updated_date timestamp not null default now();
alter table projekt_data.new_dataset_request add column assignee varchar(100);
alter table projekt_data.new_dataset_request add column data text;

alter table projekt_data.linked_dataset add column process_definition_key varchar(150) not null default 'linked-dataset-process';
alter table projekt_data.linked_dataset add column process_definition_version int4;
alter table projekt_data.linked_dataset add column status varchar(20) not null default 'DRAFT';
alter table projekt_data.linked_dataset add column functional_status varchar(50) not null default '';
alter table projekt_data.linked_dataset add column initiator varchar(50) not null default 'rudi';
alter table projekt_data.linked_dataset add column description varchar(255);
alter table projekt_data.linked_dataset add column updator varchar(50);
alter table projekt_data.linked_dataset add column creation_date timestamp not null default now();
alter table projekt_data.linked_dataset add column updated_date timestamp not null default now();
alter table projekt_data.linked_dataset add column assignee varchar(100);
alter table projekt_data.linked_dataset add column data text;

update projekt_data.project set project_status = 'VALIDATED' where project_status = 'VALIDE';
update projekt_data.project set project_status = 'CANCELLED' where project_status = 'ABANDONNE';
update projekt_data.project set project_status = 'IN_PROGRESS' where project_status = 'EN_COURS';

CREATE TABLE section_definition
(
    id bigserial not null, 
    uuid uuid not null,
    definition text NOT NULL,
    label character varying(150)  NOT NULL,
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
