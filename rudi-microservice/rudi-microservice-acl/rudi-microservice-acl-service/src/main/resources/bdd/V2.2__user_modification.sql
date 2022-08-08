ALTER TABLE acl_data.user ADD failed_attempt integer not null default 0;

ALTER TABLE acl_data.user ADD last_failed_attempt timestamp;

ALTER TABLE acl_data.user ADD last_connexion timestamp;

ALTER TABLE acl_data.user ADD account_locked BOOLEAN not null  default false;
