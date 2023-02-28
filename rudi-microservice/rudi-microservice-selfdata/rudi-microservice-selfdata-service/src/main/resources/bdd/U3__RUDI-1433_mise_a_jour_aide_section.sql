update selfdata_data.section_definition set help = substring(help, 1, 150);
alter table selfdata_data.section_definition alter column help type character varying(150);

update selfdata_data.section_definition set name = 'selfdata-information-request-process__UserTask_4__rejected'
where name = 'reject-selfdata-information-request';

DELETE
FROM selfdata_data.flyway_schema_history
WHERE version = '3';
