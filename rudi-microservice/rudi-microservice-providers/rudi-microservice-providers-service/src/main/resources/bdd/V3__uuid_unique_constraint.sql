ALTER TABLE providers_data.provider
    ADD CONSTRAINT provider_uk UNIQUE (uuid);
ALTER TABLE providers_data.node_provider
    ADD CONSTRAINT node_provider_uk UNIQUE (uuid);
