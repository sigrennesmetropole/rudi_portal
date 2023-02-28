DELETE
FROM user_role
WHERE user_fk = (SELECT id FROM acl_data.user WHERE login = 'selfdata');

DELETE
FROM "user"
WHERE login = 'selfdata';

DELETE
FROM role
WHERE code ILIKE ('MODULE_SELFDATA%');

DELETE
FROM flyway_schema_history
WHERE version = '9';
