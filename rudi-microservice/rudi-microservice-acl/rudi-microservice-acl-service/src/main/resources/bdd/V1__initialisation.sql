create table acl_data.abstract_address (id  bigserial not null, uuid uuid not null, type varchar(30) not null, address_role_fk int8, user_fk int8, primary key (id));
create table acl_data.address_role (id  bigserial not null, uuid uuid not null, code varchar(20) not null, label varchar(100), closing_date timestamp, opening_date timestamp not null, order_ int4 not null, type varchar(30) not null, primary key (id));
create table acl_data.email_address (email varchar(150) not null, id int8 not null, primary key (id));
create table acl_data.postal_address (additional_identification varchar(255), distribution_service varchar(255), locality varchar(255), recipient_identification varchar(255), street_number varchar(255), id int8 not null, primary key (id));
create table acl_data.telephone_address (phone_number varchar(20) not null, id int8 not null, primary key (id));
create table acl_data.role (id  bigserial not null, uuid uuid not null, code varchar(50) not null, label varchar(100), closing_date timestamp, opening_date timestamp not null, order_ int4 not null, primary key (id));
create table acl_data.user (id  bigserial not null, uuid uuid not null, company varchar(100), firstname varchar(30), lastname varchar(30), login varchar(100) not null, password varchar(150) not null, type varchar(255) not null, primary key (id));
create table acl_data.user_role (user_fk int8 not null, role_fk int8 not null, primary key (user_fk, role_fk));
alter table if exists acl_data.abstract_address add constraint FK5oaosawfo64gd0ucg879vxrae foreign key (address_role_fk) references acl_data.address_role;
alter table if exists acl_data.abstract_address add constraint FKsc1ml9eg7knaarhkuyg53py9e foreign key (user_fk) references acl_data.user;
alter table if exists acl_data.email_address add constraint FKryyrbe8jhs4nbj9uk5w4lfbde foreign key (id) references acl_data.abstract_address;
alter table if exists acl_data.postal_address add constraint FKjv45t9x7yau7vm6wjfii1l222 foreign key (id) references acl_data.abstract_address;
alter table if exists acl_data.telephone_address add constraint FKfohwbwly2nvy9sewvhiis2vvx foreign key (id) references acl_data.abstract_address;
alter table if exists acl_data.user_role add constraint FKa68196081fvovjhkek5m97n3y foreign key (role_fk) references acl_data.role;
alter table if exists acl_data.user_role add constraint FK859n2jvi8ivhui0rl0esws6o foreign key (user_fk) references acl_data.user;

insert into acl_data.address_role(uuid, code, label, opening_date, order_, type)
	VALUES ('6faf5005-be12-4c39-aa8b-902d74d4defa', 'LOGIN', 'Login email', timestamp '2021-01-01 01:00:00', 99, 'EMAIL'); 

insert into acl_data.role (uuid, code, label, opening_date, closing_date, order_) values 
 ( 'bedd11a0-386d-410c-ba1f-d674e67f1d54', 'ADMINISTRATOR' ,'Administrateur' , timestamp '2021-01-01 01:00:00' , null, 1),
 ( 'af8cbc17-b7e0-43c1-92e2-f42a1d18e103', 'PROVIDER' ,'Producteur de données' , timestamp '2021-01-01 01:00:00' , null, 10),
 ( '8b0f9b70-222e-4359-9ff6-0106b3d19cea', 'PROJECT_MANAGER' ,'Porteur de projet' , timestamp '2021-01-01 01:00:00' , null, 20),
 ( 'fcd893fc-a2f5-4519-8454-91a881554d1d', 'USER' ,'Utilisateur' , timestamp '2021-01-01 01:00:00' , null, 30),	
 ( 'e99e356a-b8af-4d91-8e08-5be107d08adb', 'MODULE_PROVIDER' ,'Module Provider' , timestamp '2021-01-01 01:00:00' , null, 40),
 ( '78f4a7f3-f3b0-4110-9d11-11d5f582f7f1', 'MODULE_PROVIDER_ADMINISTRATOR' ,'Module administrateur Provider' , timestamp '2021-01-01 01:00:00' , null, 50),
 ( '8ad75b0f-f171-47d5-a330-6ebabe939985', 'MODULE_KALIM' ,'Module Kalim' , timestamp '2021-01-01 01:00:00' , null, 60),
 ( '3b9e9719-c8ba-4792-af89-4da644e7b5d8', 'MODULE_KALIM_ADMINISTRATOR' ,'Module administrateur Kalim' , timestamp '2021-01-01 01:00:00' , null, 70),
 ( 'e98fa183-074a-417f-b01c-6b1df40ed66f', 'MODULE_KONSULT' ,'Module Konsult' , timestamp '2021-01-01 01:00:00' , null, 80),
 ( '0cf85f28-532c-403c-85bc-84f320602770', 'MODULE_KONSULT_ADMINISTRATOR' ,'Module administrateur Konsult' , timestamp '2021-01-01 01:00:00' , null, 90),	 
 ( 'c58c0df0-b5a3-48ce-8c75-a98833216e28', 'MODULE_ACL' ,'Module ACL' , timestamp '2021-01-01 01:00:00' , null, 100),
 ( '29d3c77b-48ab-4573-a293-553f946442b6', 'MODULE_ACL_ADMINISTRATOR' ,'Module administrateur ACL' , timestamp '2021-01-01 01:00:00' , null, 110),
 ( '536670d7-237f-4685-aea3-9d2fb0136f74', 'MODULE' ,'Module' , timestamp '2021-01-01 01:00:00' , null, 120);
 
insert into acl_data.user (uuid, company, firstname, lastname, login, password, type) values
	('b3719275-0621-4df6-b11b-8fb9157827c0', 'rudi', 'rudi', 'rudi', 'rudi', '$2a$04$O4bZyIRZSZYHfOf3CSxQV.3YksooAFMyipj17EP/fuc7DNBAAP7vq', 'ROBOT'),
	('ca86b4b6-ca68-4a92-8c0a-d5a590d13631', 'rudi', 'acl', 'acl', 'acl', '$2a$04$O4bZyIRZSZYHfOf3CSxQV.3YksooAFMyipj17EP/fuc7DNBAAP7vq', 'ROBOT'),
	('ce7fd13a-73f2-40a8-a785-7c2c4f7cb320', 'rudi', 'providers', 'providers', 'providers', '$2a$04$O4bZyIRZSZYHfOf3CSxQV.3YksooAFMyipj17EP/fuc7DNBAAP7vq', 'ROBOT'),
	('70e2b0b2-5596-4868-8512-ec96a9907b64', 'rudi', 'kalim', 'kalim', 'kalim', '$2a$04$O4bZyIRZSZYHfOf3CSxQV.3YksooAFMyipj17EP/fuc7DNBAAP7vq', 'ROBOT'),
	('6a793da2-c96b-4a5c-b7c7-cccc2ea7a63c', 'rudi', 'konsult', 'konsult', 'konsult', '$2a$04$O4bZyIRZSZYHfOf3CSxQV.3YksooAFMyipj17EP/fuc7DNBAAP7vq', 'ROBOT'),
	('5596b5b2-b227-4c74-a9a1-719e7c1008c7', 'nodestub', 'nodestub', 'nodestub', '5596b5b2-b227-4c74-a9a1-719e7c1008c7', '$2a$04$O4bZyIRZSZYHfOf3CSxQV.3YksooAFMyipj17EP/fuc7DNBAAP7vq', 'ROBOT'),
	('ca4a5ae2-c88f-415f-8c56-762bc7c2fd97', 'test', 'Michel', 'Martin', 'mm@test.fr', '$2a$04$O4bZyIRZSZYHfOf3CSxQV.3YksooAFMyipj17EP/fuc7DNBAAP7vq', 'PERSON');

insert into acl_data.user_role( user_fk, role_fk) values
	( (select id from acl_data.user where login = 'rudi'), (select id from acl_data.role where code = 'ADMINISTRATOR')),
	( (select id from acl_data.user where login = 'acl'), (select id from acl_data.role where code = 'MODULE')),
	( (select id from acl_data.user where login = 'acl'), (select id from acl_data.role where code = 'MODULE_ACL')),
	( (select id from acl_data.user where login = 'providers'), (select id from acl_data.role where code = 'MODULE')),
	( (select id from acl_data.user where login = 'providers'), (select id from acl_data.role where code = 'MODULE_PROVIDER')),
	( (select id from acl_data.user where login = 'kalim'), (select id from acl_data.role where code = 'MODULE')),
	( (select id from acl_data.user where login = 'kalim'), (select id from acl_data.role where code = 'MODULE_KALIM')),
	( (select id from acl_data.user where login = 'konsult'), (select id from acl_data.role where code = 'MODULE')),
	( (select id from acl_data.user where login = 'konsult'), (select id from acl_data.role where code = 'MODULE_KONSULT')),
	( (select id from acl_data.user where login = '5596b5b2-b227-4c74-a9a1-719e7c1008c7'), (select id from acl_data.role where code = 'PROVIDER')),
	( (select id from acl_data.user where login = 'mm@test.fr'), (select id from acl_data.role where code = 'USER'));
	
insert into acl_data.abstract_address(uuid, type, address_role_fk, user_fk)
	values ('d501c038-9d3c-401b-8ef9-0aa153e1691c', 'EMAIL', (select id from acl_data.address_role where code = 'LOGIN' and type = 'EMAIL'), (select id from acl_data.user where login = '') );
	
insert into acl_data.email_address(id, email)
    values ((select id from acl_data.abstract_address where uuid = 'd501c038-9d3c-401b-8ef9-0aa153e1691c'),'mm@test.fr');