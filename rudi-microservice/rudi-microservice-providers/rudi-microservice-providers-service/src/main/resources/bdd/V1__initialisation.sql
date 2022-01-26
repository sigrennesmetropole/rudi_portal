create table providers_data.address_role (id  bigserial not null, uuid uuid not null, code varchar(20) not null, label varchar(100), closing_date timestamp, opening_date timestamp not null, order_ int4 not null, type varchar(30) not null, primary key (id));
create table providers_data.email_address (email varchar(255), id int8 not null, primary key (id));
create table providers_data.node_provider (id  bigserial not null, uuid uuid not null, closing_date timestamp, harverstable boolean not null, notifiable boolean not null, opening_date timestamp not null, url varchar(255), version varchar(255), provider_fk int8, primary key (id));
create table providers_data.postal_address (additional_identification varchar(255), distribution_service varchar(255), locality varchar(255), recipient_identification varchar(255), street_number varchar(255), id int8 not null, primary key (id));
create table providers_data.provider (id  bigserial not null, uuid uuid not null, code varchar(20) not null, label varchar(100), closing_date timestamp, opening_date timestamp not null, order_ int4 not null, primary key (id));
create table providers_data.telephone_address (phone_number varchar(255), id int8 not null, primary key (id));
create table providers_data.web_site_address (url varchar(255), id int8 not null, primary key (id));
create table providers_data.abstract_address (id  bigserial not null, uuid uuid not null, type varchar(30) not null, address_role_fk int8, provider_fk int8, primary key (id));
alter table if exists providers_data.email_address add constraint FKggvdq19bfor5kexxhik2l2e4a foreign key (id) references abstract_address;
alter table if exists providers_data.node_provider add constraint FKs6dcnfa8w6vru7xpbj4bur3w7 foreign key (provider_fk) references providers_data.provider;
alter table if exists providers_data.postal_address add constraint FK98qsp9or9y6lkk2sojx2whw2x foreign key (id) references abstract_address;
alter table if exists providers_data.telephone_address add constraint FKj4u3powot25p1hyld4sqa20t9 foreign key (id) references abstract_address;
alter table if exists providers_data.web_site_address add constraint FK1sghvtgeejnv5y3eigkprpjqq foreign key (id) references abstract_address;
alter table if exists providers_data.abstract_address add constraint FK1aovhdd1efm0l7eau06ya8t11 foreign key (address_role_fk) references providers_data.address_role;
alter table if exists providers_data.abstract_address add constraint FK14sktnct3mlngr0kuqxqmwgxi foreign key (provider_fk) references providers_data.provider;

insert into providers_data.provider (uuid, code, label, opening_date, order_)
	values ('5596b5b2-b227-4c74-a9a1-719e7c1008c7', 'NODE_STUB', 'NodeStub', timestamp '2021-01-01 01:00:00', 99);
	
insert into providers_data.node_provider (uuid, harverstable, notifiable, opening_date, url, version, provider_fk)
    values ('5596b5b2-b227-4c74-a9a1-719e7c1008c7', true, true, timestamp '2021-01-01 01:00:00', 'http://10.50.1.45:28001/nodestub', 'v1', (select id from providers_data.provider where code = 'NODE_STUB'));