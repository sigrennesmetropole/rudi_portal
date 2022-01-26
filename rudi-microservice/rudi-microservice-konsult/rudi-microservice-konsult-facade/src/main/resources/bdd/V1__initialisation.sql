CREATE TABLE IF NOT EXISTS konsult_data.konsult (id bigserial NOT NULL, uuid uuid not null,
	comment varchar(20) not null, PRIMARY KEY (id));