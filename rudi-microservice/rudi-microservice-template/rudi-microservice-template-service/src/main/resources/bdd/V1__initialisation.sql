CREATE TABLE IF NOT EXISTS template_data.template (id bigserial NOT NULL, uuid uuid not null, 
	comment varchar(20) not null, PRIMARY KEY (id));