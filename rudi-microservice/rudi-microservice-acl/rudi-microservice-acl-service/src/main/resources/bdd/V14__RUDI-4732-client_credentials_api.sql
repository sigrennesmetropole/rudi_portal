create table acl_data.project_key (id  bigserial not null, uuid uuid not null, creation_date timestamp not null, expiration_date timestamp, name varchar(256) not null, user_fk int8, project_keystore_fk int8, primary key (id));
create table acl_data.project_keystore (id  bigserial not null, uuid uuid not null, project_uuid uuid not null, primary key (id));

alter table if exists acl_data.project_key add constraint UK_3j4963hdb2mvdihkk465gnuir unique (uuid);
alter table if exists acl_data.project_keystore add constraint UK_tlis6y4yy8ru6a5h5w7firyey unique (uuid);

alter table if exists acl_data.project_key add constraint FKqak1gdr9yumdj4u6br1ahd3x2 foreign key (user_fk) references acl_data.user_;
alter table if exists acl_data.project_key add constraint FKjdlntfcfekhiey1etyil87nhp foreign key (project_keystore_fk) references acl_data.project_keystore;
