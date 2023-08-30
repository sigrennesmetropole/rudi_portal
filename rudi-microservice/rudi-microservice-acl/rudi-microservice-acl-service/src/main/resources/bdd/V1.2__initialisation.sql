-- um_user (UM_ID, um_user_name varchar(100), UM_USER_PASSWORD, UM_SALT_VALUE, UM_REQUIRE_CHANGE bool, UM_CHANGED_TIME, UM_TENANT_ID);
create or replace view acl_data.um_user as 
	select u.id as UM_ID, u.login as um_user_name, u.password as UM_USER_PASSWORD, '' as UM_SALT_VALUE, '0' as UM_REQUIRE_CHANGE, timestamp '2021-01-01 01:00:00' as UM_CHANGED_TIME, -1234 as UM_TENANT_ID
	from acl_data.user_ u;

-- UM_ROLE (UM_ROLE_NAME,UM_TENANT_ID,UM_SHARED_ROLE 0 ou 1);
create or replace view acl_data.um_role as 
	select r.id as UM_ID, r.code as UM_ROLE_NAME, -1234 as UM_TENANT_ID, 0 as UM_SHARED_ROLE
	from acl_data.role r
	where r.opening_date < CURRENT_TIMESTAMP and ( r.closing_date is null or r.closing_date > CURRENT_TIMESTAMP );

-- UM_USER_ROLE (UM_ID, UM_USER_ID, UM_ROLE_ID, UM_TENANT_ID);
create or replace view acl_data.UM_USER_ROLE as 
	select ur.user_fk as UM_USER_ID, ur.role_fk as UM_ROLE_ID, -1234 as UM_TENANT_ID
	from acl_data.user_role ur;

-- UM_SHARED_USER_ROLE (UM_ROLE_ID, UM_USER_ID, UM_USER_TENANT_ID, UM_ROLE_TENANT_ID);
create or replace view acl_data.UM_SHARED_USER_ROLE as 
	select ur.user_fk as UM_USER_ID, ur.role_fk as UM_ROLE_ID, -1234 as UM_USER_TENANT_ID, -1234 as UM_ROLE_TENANT_ID
	from acl_data.user_role ur;

--UM_USER_ATTRIBUTE(UM_ATTR_NAME, UM_ATTR_VALUE, UM_USER_ID, UM_PROFILE_ID, UM_TENANT_ID);
create or replace view acl_data.UM_USER_ATTRIBUTE as 
	select u.id as UM_USER_ID, 'default'  as UM_PROFILE_ID, -1234 as UM_TENANT_ID, 'scimId' as UM_ATTR_NAME, u.uuid::text as UM_ATTR_VALUE
	from acl_data.user_ u;
	
-- UM_HYBRID_ROLE(UM_ID,UM_ROLE_NAME,UM_TENANT_ID);
create or replace view acl_data.UM_HYBRID_ROLE as 
	select r.id as UM_ID, r.code  as UM_ROLE_NAME, -1234 as UM_TENANT_ID
	from acl_data.role r;	
