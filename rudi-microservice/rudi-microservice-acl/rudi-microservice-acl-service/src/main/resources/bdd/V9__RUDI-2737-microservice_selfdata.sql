INSERT INTO role (uuid, code, label, opening_date, closing_date, order_)
VALUES ('a638562f-d240-430b-86c4-0e7c39a079cb', 'MODULE_SELFDATA', 'Microservice selfdata', current_timestamp, NULL,
        (SELECT max(order_) FROM acl_data.role) + 1),
       ('d4cfa1d8-081b-42bf-9f87-24958b1cf4c2', 'MODULE_SELFDATA_ADMINISTRATOR',
        'Administrateur du microservice selfdata', current_timestamp, NULL,
        (SELECT max(order_) FROM acl_data.role) + 2);

INSERT INTO user_ (uuid, company, firstname, lastname, login, password, type)
VALUES ('3551e95b-9f05-43e7-8cb4-e6e0b7b13d62', 'rudi', 'selfdata', 'selfdata', 'selfdata',
        '$2a$04$O4bZyIRZSZYHfOf3CSxQV.3YksooAFMyipj17EP/fuc7DNBAAP7vq', 'ROBOT');

INSERT INTO user_role(user_fk, role_fk)
VALUES ((SELECT id FROM acl_data.user_ WHERE login = 'selfdata'), (SELECT id FROM acl_data.role WHERE code = 'MODULE')),
       ((SELECT id FROM acl_data.user_ WHERE login = 'selfdata'),
        (SELECT id FROM acl_data.role WHERE code = 'MODULE_SELFDATA'));
