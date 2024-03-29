create table kos_data.skos_concept (id  bigserial not null, uuid uuid not null, code varchar(20) not null, label varchar(100), closing_date timestamp, opening_date timestamp not null, order_ int4 not null, concept_icon varchar(255) not null, concept_role varchar(255) not null, concept_uri varchar(255) not null, of_scheme_fk int8, primary key (id));
create table kos_data.skos_concept_alternate_label (skos_concept_fk int8 not null, skos_concept_translation_fk int8 not null, primary key (skos_concept_fk, skos_concept_translation_fk));
create table kos_data.skos_concept_definition (skos_concept_fk int8 not null, skos_concept_translation_fk int8 not null, primary key (skos_concept_fk, skos_concept_translation_fk));
create table kos_data.skos_concept_example (skos_concept_fk int8 not null, skos_concept_translation_fk int8 not null, primary key (skos_concept_fk, skos_concept_translation_fk));
create table kos_data.skos_concept_hidden_label (skos_concept_fk int8 not null, skos_concept_translation_fk int8 not null, primary key (skos_concept_fk, skos_concept_translation_fk));
create table kos_data.skos_concept_note (skos_concept_fk int8 not null, skos_concept_translation_fk int8 not null, primary key (skos_concept_fk, skos_concept_translation_fk));
create table kos_data.skos_concept_prefered_label (skos_concept_fk int8 not null, skos_concept_translation_fk int8 not null, primary key (skos_concept_fk, skos_concept_translation_fk));
create table kos_data.skos_concept_translation (id  bigserial not null, uuid uuid not null, lang varchar(10), text varchar(255), primary key (id));
create table kos_data.skos_relation_concept (id  bigserial not null, uuid uuid not null, type varchar(25), skos_concept_fk int8, relation_concept_fk int8, primary key (id));
create table kos_data.skos_scheme (id  bigserial not null, uuid uuid not null, code varchar(20) not null, label varchar(100), closing_date timestamp, opening_date timestamp not null, order_ int4 not null, role varchar(50), uri varchar(255), primary key (id));
create table kos_data.skos_scheme_top_concept (skos_scheme_fk int8 not null, top_concept_fk int8 not null, primary key (skos_scheme_fk, top_concept_fk));
create table kos_data.skos_scheme_translation (id  bigserial not null, uuid uuid not null, lang varchar(10), text varchar(255), skos_scheme_fk int8, primary key (id));
alter table if exists kos_data.skos_concept add constraint FK9x8y5klqg1nkyo8eg44hvo67k foreign key (of_scheme_fk) references kos_data.skos_scheme;
alter table if exists kos_data.skos_concept_alternate_label add constraint FK5w3jltj5lorskwvoyak0vw5ti foreign key (skos_concept_translation_fk) references kos_data.skos_concept_translation;
alter table if exists kos_data.skos_concept_alternate_label add constraint FK16fwluqv4tiuj6ad7rep0uj4g foreign key (skos_concept_fk) references kos_data.skos_concept;
alter table if exists kos_data.skos_concept_definition add constraint FKfojxnv8459tgle6tw4fpramie foreign key (skos_concept_translation_fk) references kos_data.skos_concept_translation;
alter table if exists kos_data.skos_concept_definition add constraint FKlkeihncg9emj4y026cvda9hr9 foreign key (skos_concept_fk) references kos_data.skos_concept;
alter table if exists kos_data.skos_concept_example add constraint FKeghrhfb7w86b1fnu2gf613yd3 foreign key (skos_concept_translation_fk) references kos_data.skos_concept_translation;
alter table if exists kos_data.skos_concept_example add constraint FKkh1uat55jnyajuhhf1xfk1wu2 foreign key (skos_concept_fk) references kos_data.skos_concept;
alter table if exists kos_data.skos_concept_hidden_label add constraint FKgbvhhkxb24s0crj6nh8fugdh3 foreign key (skos_concept_translation_fk) references kos_data.skos_concept_translation;
alter table if exists kos_data.skos_concept_hidden_label add constraint FK41hjnr6btwypb6e0ymjlorl55 foreign key (skos_concept_fk) references kos_data.skos_concept;
alter table if exists kos_data.skos_concept_note add constraint FKnt3v69tstrkdyj571hx4x6qpw foreign key (skos_concept_translation_fk) references kos_data.skos_concept_translation;
alter table if exists kos_data.skos_concept_note add constraint FKkxbxx777soemem4k51eihb2g8 foreign key (skos_concept_fk) references kos_data.skos_concept;
alter table if exists kos_data.skos_concept_prefered_label add constraint FKses3y4s1jce92yctyan9tpqrl foreign key (skos_concept_translation_fk) references kos_data.skos_concept_translation;
alter table if exists kos_data.skos_concept_prefered_label add constraint FKua5g2hw5fe3vmh5tsbj8kk59 foreign key (skos_concept_fk) references kos_data.skos_concept;
alter table if exists kos_data.skos_relation_concept add constraint FK2rex8430yc5cgg4vhej7h90eu foreign key (skos_concept_fk) references kos_data.skos_concept;
alter table if exists kos_data.skos_relation_concept add constraint FKqewyiaohccduv59o1wocv49r foreign key (relation_concept_fk) references kos_data.skos_concept;
alter table if exists kos_data.skos_scheme_top_concept add constraint FKr0mqn9ctrlfta3wf7va46g6sh foreign key (top_concept_fk) references kos_data.skos_concept;
alter table if exists kos_data.skos_scheme_top_concept add constraint FKo3bg940m3p7x5dc4jhj7b3p08 foreign key (skos_scheme_fk) references kos_data.skos_scheme;
alter table if exists kos_data.skos_scheme_translation add constraint FKq9epi1b9edu0wlgvmydc6j21d foreign key (skos_scheme_fk) references kos_data.skos_scheme;
