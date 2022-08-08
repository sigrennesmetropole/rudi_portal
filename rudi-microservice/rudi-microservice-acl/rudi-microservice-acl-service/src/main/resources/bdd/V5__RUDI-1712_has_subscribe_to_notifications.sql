ALTER TABLE "user"
    ADD COLUMN has_subscribe_to_notifications boolean NOT NULL DEFAULT FALSE;
COMMENT ON COLUMN "user".has_subscribe_to_notifications
    IS 'Peut-on contacter l''utilisateur sur son adresse mail ?';

ALTER TABLE account_registration
    ADD COLUMN has_subscribe_to_notifications boolean NOT NULL DEFAULT FALSE;
COMMENT ON COLUMN account_registration.has_subscribe_to_notifications
    IS 'Peut-on contacter l''utilisateur sur son adresse mail ?';
