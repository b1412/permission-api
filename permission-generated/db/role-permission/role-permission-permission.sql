INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (1,0,'Index RolePermission','/v[\\d]+/role-permission','RolePermission','GET',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (2,0,'Create RolePermission','/v[\\d]+/role-permission','RolePermission','POST',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (3,0,'Read RolePermission','/v[\\d]+/role-permission/[\\d]+','RolePermission','GET',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (4,0,'Update RolePermission','/v[\\d]+/role-permission/[\\d]+','RolePermission','PUT',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (5,0,'Delete a RolePermission','/v[\\d]+/role-permission/[\\d]+','RolePermission','DELETE',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (6,0,'Delete all RolePermission','/v[\\d]+/role-permission/clear','RolePermission','DELETE',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (7,0,'Excel RolePermission','/v[\\d]+/role-permission/excel','RolePermission','GET',1, 1);
