DROP TABLE projekt_data.dataset_request ;

-- Suppression de la migration flyway
delete
from projekt_data.flyway_schema_history
where version = '6';