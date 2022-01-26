insert into acl_data.role (uuid, code, label, opening_date, closing_date, order_) values 
 ( 'f5611fe6-b73c-4776-8d48-74265c6e39f6', 'MODULE_KOS_ADMINISTRATOR' ,'Module administrateur Kos' , timestamp '2021-01-01 01:00:00' , null, 111);

insert into acl_data.user (uuid, company, firstname, lastname, login, password, type) values
	('ec21d59b-ea27-42d1-ae17-6a63fd79ed6e', 'rudi', 'kos', 'kos', 'kos', '$2a$04$O4bZyIRZSZYHfOf3CSxQV.3YksooAFMyipj17EP/fuc7DNBAAP7vq', 'ROBOT');

insert into acl_data.user_role( user_fk, role_fk) values
	( (select id from acl_data.user where login = 'kos'), (select id from acl_data.role where code = 'MODULE')),
	( (select id from acl_data.user where login = 'kos'), (select id from acl_data.role where code = 'MODULE_KOS_ADMINISTRATOR'));