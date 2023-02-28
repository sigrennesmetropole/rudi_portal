-- Le rôle MODULE_SELFDATA_ADMINISTRATOR avait été déjà créé par le fichier de flyway RUDI-2737 --

INSERT INTO acl_data.user (uuid, company, firstname, lastname, login, password, type)
VALUES ('4da48781-23b8-41cc-a0d6-cf866f9a56ae', 'rudi', 'selfdata', 'wso2', 'selfdata-wso2-user',
        '$2a$04$O4bZyIRZSZYHfOf3CSxQV.3YksooAFMyipj17EP/fuc7DNBAAP7vq', 'ROBOT');

INSERT INTO acl_data.user_role(user_fk, role_fk)
VALUES ((SELECT id FROM acl_data.user WHERE login = 'selfdata-wso2-user'),
        (SELECT id FROM acl_data.role WHERE code = 'MODULE_SELFDATA_ADMINISTRATOR'));