create table acl_data.reset_password_request
(
    id            bigserial not null,
    uuid          uuid not null,
    user_uuid     uuid not null,
    token         uuid not null,
    creation_date timestamp not null,
    primary key (id)
);