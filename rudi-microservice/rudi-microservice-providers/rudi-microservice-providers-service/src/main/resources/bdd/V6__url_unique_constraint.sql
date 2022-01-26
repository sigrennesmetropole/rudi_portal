ALTER TABLE providers_data.node_provider
    ADD CONSTRAINT node_provider_uk_url UNIQUE (url);
