insert into acl_data.role (uuid, code, label, opening_date, closing_date, order_) values 
 ( 'c9f3cce2-2d8d-4b2e-8d03-43bef39a989d', 'ANONYMOUS' ,'Anonyme' , timestamp '2021-01-01 01:00:00' , null, 130);
 
insert into acl_data.user_ (uuid, company, firstname, lastname, login, password, type) values 
 ('2d7784be-6f1e-492f-9a0e-7597747742a1', 'rudi', 'anonymous', 'anonymous', 'anonymous', '', 'PERSON');
 
insert into acl_data.user_role( user_fk, role_fk) values
	( (select id from acl_data.user_ where login = 'anonymous'), (select id from acl_data.role where code = 'ANONYMOUS')); 
 
 