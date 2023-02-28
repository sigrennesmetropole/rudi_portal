DROP TABLE document;

DELETE
FROM flyway_schema_history
WHERE version = '2';
