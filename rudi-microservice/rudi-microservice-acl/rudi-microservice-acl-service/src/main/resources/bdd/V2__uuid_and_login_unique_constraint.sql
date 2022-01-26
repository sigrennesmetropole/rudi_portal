ALTER TABLE acl_data.user
    ADD CONSTRAINT user_uk_uuid UNIQUE (uuid);
ALTER TABLE acl_data.user
    ADD CONSTRAINT user_uk_login UNIQUE (login);
