insert into acl_data.role (uuid, code, label, opening_date, closing_date, order_) values 
 ( 'd77e9af8-8e78-4f64-8865-57e7c9c75c55', 'MODULE_APIGATEWAY_ADMINISTRATOR' ,'Module administrateur API Gateway' , timestamp '2021-01-01 01:00:00' , null, 111);

insert into acl_data.user_ (uuid, company, firstname, lastname, login, password, type) values
	('f6ec5bd5-bcf9-4d06-a0cf-20402465310b', 'rudi', 'apigateway', 'apigateway', 'apigateway', '$2a$04$O4bZyIRZSZYHfOf3CSxQV.3YksooAFMyipj17EP/fuc7DNBAAP7vq', 'ROBOT');

insert into acl_data.user_role( user_fk, role_fk) values
	( (select id from acl_data.user_ where login = 'apigateway'), (select id from acl_data.role where code = 'MODULE')),
	( (select id from acl_data.user_ where login = 'apigateway'), (select id from acl_data.role where code = 'MODULE_APIGATEWAY_ADMINISTRATOR'))
	;
	