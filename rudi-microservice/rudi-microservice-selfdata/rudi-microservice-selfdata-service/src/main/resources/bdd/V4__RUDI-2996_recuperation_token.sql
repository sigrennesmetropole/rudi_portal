CREATE TABLE IF NOT EXISTS selfdata_data.selfdata_token_tuple
(
    id               bigserial NOT NULL,
    uuid             uuid      NOT NULL,
    token            uuid      NOT NULL UNIQUE,
    dataset_uuid     uuid      NOT NULL,
    user_uuid        uuid      NOT NULL,
    node_provider_id uuid      NOT NULL
);