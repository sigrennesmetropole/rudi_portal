ALTER TABLE "user"
    DROP COLUMN has_subscribe_to_notifications;

ALTER TABLE account_registration
    DROP COLUMN has_subscribe_to_notifications;

DELETE
FROM flyway_schema_history
WHERE version = '5';
