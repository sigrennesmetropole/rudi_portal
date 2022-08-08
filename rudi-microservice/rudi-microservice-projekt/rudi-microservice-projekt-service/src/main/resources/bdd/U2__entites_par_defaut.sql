TRUNCATE projekt_data.confidentiality CASCADE;

DELETE
FROM projekt_data.flyway_schema_history
where version = '2';
