CREATE TABLE IF NOT EXISTS acl_data.user_attribute_name
(
    name character varying(75),
    value_ character varying(255)
);

delete from acl_data.user_attribute_name;
insert into acl_data.user_attribute_name  (name,value_) values ('uid','login');
insert into acl_data.user_attribute_name  (name,value_) values ('scimId','uuid');

CREATE OR REPLACE VIEW acl_data.um_user_attribute
 AS
 SELECT u.id AS um_user_id,
    'default'::text AS um_profile_id,
    '-1234'::integer AS um_tenant_id,
    n.name::text AS um_attr_name,
    case 
		when n.value_ = 'uuid' then u.uuid::text
	  	when n.value_ = 'login' then u.login::text
		else u.login::text
	end AS um_attr_value
 FROM acl_data.user_ u, acl_data.user_attribute_name n;