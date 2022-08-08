ALTER TABLE strukture_data.provider
    ADD CONSTRAINT provider_uk UNIQUE (uuid);
ALTER TABLE strukture_data.node_provider
    ADD CONSTRAINT node_provider_uk UNIQUE (uuid);
