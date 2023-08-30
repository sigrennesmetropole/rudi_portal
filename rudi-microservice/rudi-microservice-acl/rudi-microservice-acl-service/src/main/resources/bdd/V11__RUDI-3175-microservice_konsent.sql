INSERT INTO role (uuid, code, label, opening_date, closing_date, order_)
VALUES ('9d8b80a1-919d-4447-bebb-abb3d6a4743b', 'MODULE_KONSENT', 'Microservice konsent', current_timestamp, NULL,
        (SELECT max(order_) FROM acl_data.role) + 1),
       ('635cf9a5-73ac-4a30-a7d5-592d9cdd9827', 'MODULE_KONSENT_ADMINISTRATOR',
        'Administrateur du microservice konsent', current_timestamp, NULL,
        (SELECT max(order_) FROM acl_data.role) + 2);

INSERT INTO user_ (uuid, company, firstname, lastname, login, password, type)
VALUES ('27e51f99-f878-41bd-9aa4-c037ad26b6a2', 'rudi', 'konsent', 'konsent', 'konsent',
        '$2a$04$O4bZyIRZSZYHfOf3CSxQV.3YksooAFMyipj17EP/fuc7DNBAAP7vq', 'ROBOT');

INSERT INTO user_role(user_fk, role_fk)
VALUES ((SELECT id FROM acl_data.user_ WHERE login = 'konsent'), (SELECT id FROM acl_data.role WHERE code = 'MODULE')),
       ((SELECT id FROM acl_data.user_ WHERE login = 'konsent'),
        (SELECT id FROM acl_data.role WHERE code = 'MODULE_KONSENT'));
