ALTER TABLE strukture_data.node_provider
    RENAME COLUMN harverstable TO harvestable;
ALTER TABLE strukture_data.node_provider
    ADD harvesting_cron varchar(998) NULL;
ALTER TABLE strukture_data.node_provider
    ADD last_harvesting_date timestamp NULL;
