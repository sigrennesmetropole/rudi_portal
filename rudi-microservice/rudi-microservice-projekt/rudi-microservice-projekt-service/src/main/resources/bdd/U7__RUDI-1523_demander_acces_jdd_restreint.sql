DROP TABLE projekt_data.linked_dataset ;

-- Suppression de la migration flyway
delete
from projekt_data.flyway_schema_history
where version = '7';