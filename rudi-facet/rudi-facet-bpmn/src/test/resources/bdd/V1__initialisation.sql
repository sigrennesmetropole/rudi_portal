create table test_asset_description (
	id  bigserial not null, 
	uuid uuid not null,
	process_definition_key varchar(150) not null,
	process_definition_version int4,
	status varchar(20) not null,
	functional_status varchar(50) not null,
	initiator varchar(50) not null,
	updator varchar(50),
	creation_date timestamp not null,
	updated_date timestamp not null,
	description varchar(1024),
	assignee varchar(100),
	data text,
	a varchar(12),
	primary key (id)
);


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

