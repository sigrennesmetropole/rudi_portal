update projekt_data.section_definition set help = substring(help, 1, 150);
alter table projekt_data.section_definition alter column help type character varying(150);

update projekt_data.section_definition set name = 'linked-dataset-process__UserTask_1__canceled'
where name = 'cancel-linked-dataset';

update projekt_data.section_definition set name = 'linked-dataset-process__UserTask_1__validated'
where name = 'validate-linked-dataset';

delete
from projekt_data.flyway_schema_history
where version = '17';
