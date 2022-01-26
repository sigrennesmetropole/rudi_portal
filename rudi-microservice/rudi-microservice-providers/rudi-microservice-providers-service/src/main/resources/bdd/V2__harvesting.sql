ALTER TABLE providers_data.node_provider
    RENAME COLUMN harverstable TO harvestable;
ALTER TABLE providers_data.node_provider
    ADD harvesting_cron varchar(998) NULL;
ALTER TABLE providers_data.node_provider
    ADD last_harvesting_date timestamp NULL;
