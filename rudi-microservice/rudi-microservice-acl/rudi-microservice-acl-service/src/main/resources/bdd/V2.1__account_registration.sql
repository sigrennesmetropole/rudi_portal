create table acl_data.account_registration
(
    id            bigserial    not null,
    uuid          uuid         not null,
    firstname     varchar(30),
    lastname      varchar(30),
    login         varchar(100) not null,
    password      varchar(150) not null,
    creation_date timestamp,
    token varchar(150),
    primary key (id)
);
