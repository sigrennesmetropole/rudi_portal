insert into acl_data.role (uuid, code, label, opening_date, closing_date, order_ ) values
( '9c11e4d6-854e-453e-9913-4d244960c449', 'MODERATOR' ,'Animateur' , CURRENT_TIMESTAMP , null, (select max(order_) from acl_data.role) + 1);
