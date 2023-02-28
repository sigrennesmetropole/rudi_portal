-- Augmentation de la taille du champ "aide" pour une section
alter table selfdata_data.section_definition alter column help type character varying(300);

update selfdata_data.section_definition set name = 'reject-selfdata-information-request'
where name = 'selfdata-information-request-process__UserTask_4__rejected';
