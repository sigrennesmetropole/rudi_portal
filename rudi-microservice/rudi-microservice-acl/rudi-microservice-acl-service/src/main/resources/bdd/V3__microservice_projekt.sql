insert into acl_data.role (uuid, code, label, opening_date, closing_date, order_) values
( 'e8d4efc1-2d34-4ac6-9d33-413ad4635b71', 'PROJECT_MANAGER' ,'Porteur de projet' , CURRENT_TIMESTAMP , null, (select max(order_) from acl_data.role) + 1),
( '97ec66bc-0374-41e2-97f9-27c8a44a5062', 'MODULE_PROJEKT' ,'Microservice projekt' , CURRENT_TIMESTAMP , null, (select max(order_) from acl_data.role) + 2),
( '97ec66bc-0374-41e2-97f9-27c8a44a5062', 'MODULE_PROJEKT_ADMINISTRATOR' ,'Administrateur du microservice projekt' , CURRENT_TIMESTAMP , null, (select max(order_) from acl_data.role) + 3);

insert into acl_data.user_ (uuid, company, firstname, lastname, login, password, type) values
('3de9005a-7ef3-4c8e-a81e-c3e9f0fe0848', 'rudi', 'projekt', 'projekt', 'projekt', '$2a$04$O4bZyIRZSZYHfOf3CSxQV.3YksooAFMyipj17EP/fuc7DNBAAP7vq', 'ROBOT');

insert into acl_data.user_role( user_fk, role_fk) values
( (select id from acl_data.user_ where login = 'projekt'), (select id from acl_data.role where code = 'MODULE')),
( (select id from acl_data.user_ where login = 'projekt'), (select id from acl_data.role where code = 'MODULE_PROJEKT'));
