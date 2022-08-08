ALTER TABLE strukture_data.node_provider
    RENAME COLUMN harvestable TO harverstable;
ALTER TABLE strukture_data.node_provider
    DROP COLUMN harvesting_cron;
ALTER TABLE strukture_data.node_provider
    DROP COLUMN last_harvesting_date;
