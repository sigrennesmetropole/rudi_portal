-- Augmentation de la taille du champ "aide" pour une section
alter table projekt_data.section_definition alter column help type character varying(300);

update projekt_data.section_definition set name = 'cancel-linked-dataset'
where name = 'linked-dataset-process__UserTask_1__canceled';

update projekt_data.section_definition set name = 'validate-linked-dataset'
where name = 'linked-dataset-process__UserTask_1__validated';

-- TODO script U_ + script renommage des section_definition
