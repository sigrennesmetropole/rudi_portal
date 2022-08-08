create table projekt_data.abstract_address
(
    id              bigserial    not null,
    uuid            uuid         not null,
    type            varchar(255) not null,
    address_role_fk int8,
    organization_fk int8,
    primary key (id)
);
create table projekt_data.address_role
(
    id           bigserial    not null,
    uuid         uuid         not null,
    code         varchar(30)  not null,
    label        varchar(100),
    closing_date timestamp,
    opening_date timestamp    not null,
    order_       int4         not null,
    type         varchar(255) not null,
    primary key (id)
);
create table projekt_data.confidentiality
(
    id           bigserial   not null,
    uuid         uuid        not null,
    code         varchar(30) not null,
    label        varchar(100),
    closing_date timestamp,
    opening_date timestamp   not null,
    order_       int4        not null,
    primary key (id)
);
create table projekt_data.email_address
(
    email varchar(150) not null,
    id    int8         not null,
    primary key (id)
);
create table projekt_data.manager
(
    id        bigserial not null,
    uuid      uuid      not null,
    user_uuid uuid      not null,
    primary key (id)
);
create table projekt_data.organization
(
    id           bigserial   not null,
    uuid         uuid        not null,
    code         varchar(30) not null,
    label        varchar(100),
    closing_date timestamp,
    opening_date timestamp   not null,
    order_       int4        not null,
    primary key (id)
);
create table projekt_data.organization_manager
(
    organization_fk int8 not null,
    manager_fk      int8 not null,
    primary key (organization_fk, manager_fk)
);
create table projekt_data.postal_address
(
    additional_identification varchar(255),
    distribution_service      varchar(255),
    locality                  varchar(255),
    recipient_identification  varchar(255),
    street_number             varchar(255),
    id                        int8 not null,
    primary key (id)
);
create table projekt_data.project
(
    id                          bigserial    not null,
    uuid                        uuid         not null,
    created                     timestamp    not null,
    desired_support_description varchar(150),
    detailed_territorial_scale  varchar(150),
    expected_completion_date    varchar(50),
    description                 text         not null,
    modified                    timestamp    not null,
    owner_type                  varchar(255) not null,
    status                      varchar(255) not null,
    title                       varchar(150) not null,
    confidentiality_fk          int8         not null,
    manager_fk                  int8         not null,
    organization_fk             int8,
    territorial_scale_fk        int8,
    type_fk                     int8,
    primary key (id)
);
create table projekt_data.project_audience
(
    project_fk        int8 not null,
    skos_concept_code varchar(30)
);
create table projekt_data.project_keyword
(
    project_fk        int8 not null,
    skos_concept_code varchar(30)
);
create table projekt_data.project_support
(
    project_fk int8 not null,
    support_fk int8 not null,
    primary key (project_fk, support_fk)
);
create table projekt_data.project_theme
(
    project_fk        int8 not null,
    skos_concept_code varchar(30)
);
create table projekt_data.project_type
(
    id           bigserial   not null,
    uuid         uuid        not null,
    code         varchar(30) not null,
    label        varchar(100),
    closing_date timestamp,
    opening_date timestamp   not null,
    order_       int4        not null,
    primary key (id)
);
create table projekt_data.support
(
    id           bigserial   not null,
    uuid         uuid        not null,
    code         varchar(30) not null,
    label        varchar(100),
    closing_date timestamp,
    opening_date timestamp   not null,
    order_       int4        not null,
    primary key (id)
);
create table projekt_data.telephone_address
(
    phone_number varchar(20) not null,
    id           int8        not null,
    primary key (id)
);
create table projekt_data.territorial_scale
(
    id           bigserial   not null,
    uuid         uuid        not null,
    code         varchar(30) not null,
    label        varchar(100),
    closing_date timestamp,
    opening_date timestamp   not null,
    order_       int4        not null,
    primary key (id)
);
alter table if exists projekt_data.abstract_address
    add constraint UK_bxkirx3n1o3wn4oq9b5spnqlb unique (uuid);
alter table if exists projekt_data.address_role
    add constraint UK_six8gx1foeclrpufdvyl6c4y2 unique (uuid);
alter table if exists projekt_data.confidentiality
    add constraint UK_e3w3hnmur2wtwbieg6jvh7dfv unique (uuid);
alter table if exists projekt_data.manager
    add constraint UK_lvhb7ue2o30lga4i5cfj6e0mc unique (uuid);
alter table if exists projekt_data.manager
    add constraint UK_cuexdnaxs0kjd1epmkkq9ua7e unique (user_uuid);
alter table if exists projekt_data.organization
    add constraint UK_s9xj0yg0stek0h7hedcn2qro3 unique (uuid);
alter table if exists projekt_data.project
    add constraint UK_97y7t9p5i0bfhc075fypbxjvi unique (uuid);
alter table if exists projekt_data.project_type
    add constraint UK_msc116aem772y01gv6o87yp05 unique (uuid);
alter table if exists projekt_data.support
    add constraint UK_2flyvk9qjri3bmplww4hxm74y unique (uuid);
alter table if exists projekt_data.territorial_scale
    add constraint UK_6m7dg4h6xqbg6d8upw0pthpk unique (uuid);
alter table if exists projekt_data.abstract_address
    add constraint FK5oaosawfo64gd0ucg879vxrae foreign key (address_role_fk) references projekt_data.address_role;
alter table if exists projekt_data.abstract_address
    add constraint FKtqff37aomot2i894duj1c2tmm foreign key (organization_fk) references projekt_data.organization;
alter table if exists projekt_data.email_address
    add constraint FKryyrbe8jhs4nbj9uk5w4lfbde foreign key (id) references projekt_data.abstract_address;
alter table if exists projekt_data.organization_manager
    add constraint FKi9pki78hpslknel729u78jb9w foreign key (manager_fk) references projekt_data.manager;
alter table if exists projekt_data.organization_manager
    add constraint FK8vj683awo8calbtes068km4ir foreign key (organization_fk) references projekt_data.organization;
alter table if exists projekt_data.postal_address
    add constraint FKjv45t9x7yau7vm6wjfii1l222 foreign key (id) references projekt_data.abstract_address;
alter table if exists projekt_data.project
    add constraint FKnufrdx5jo3xj6hr9c6jmsrnps foreign key (confidentiality_fk) references projekt_data.confidentiality;
alter table if exists projekt_data.project
    add constraint FK16iqcnnmrl9xskpims6nw8pc1 foreign key (manager_fk) references projekt_data.manager;
alter table if exists projekt_data.project
    add constraint FKjjm93q3pxup45kqa1j670x8n4 foreign key (organization_fk) references projekt_data.organization;
alter table if exists projekt_data.project
    add constraint FKfhcx78a9kuhucc3e8p8ctkbnu foreign key (territorial_scale_fk) references projekt_data.territorial_scale;
alter table if exists projekt_data.project
    add constraint FKl5qia8ckfi6lx03q1omk4xaq foreign key (type_fk) references projekt_data.project_type;
alter table if exists projekt_data.project_audience
    add constraint FKrlkggrowlus4fdwr2am22qr8n foreign key (project_fk) references projekt_data.project;
alter table if exists projekt_data.project_keyword
    add constraint FK791pfive13r2w40t3ia7cltp3 foreign key (project_fk) references projekt_data.project;
alter table if exists projekt_data.project_support
    add constraint FK798vfsuxnd3e0ydfqa5u3ksef foreign key (support_fk) references projekt_data.support;
alter table if exists projekt_data.project_support
    add constraint FKahm58ssbnc4r08x1tuahfcx33 foreign key (project_fk) references projekt_data.project;
alter table if exists projekt_data.project_theme
    add constraint FK8q3pk8hnnl7tf4u0niql7xjwx foreign key (project_fk) references projekt_data.project;
alter table if exists projekt_data.telephone_address
    add constraint FKfohwbwly2nvy9sewvhiis2vvx foreign key (id) references projekt_data.abstract_address;
