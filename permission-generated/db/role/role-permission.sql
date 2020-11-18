INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (1,0,'Index Role','/v[\\d]+/role','Role','GET',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (2,0,'Create Role','/v[\\d]+/role','Role','POST',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (3,0,'Read Role','/v[\\d]+/role/[\\d]+','Role','GET',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (4,0,'Update Role','/v[\\d]+/role/[\\d]+','Role','PUT',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (5,0,'Delete a Role','/v[\\d]+/role/[\\d]+','Role','DELETE',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (6,0,'Delete all Role','/v[\\d]+/role/clear','Role','DELETE',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (7,0,'Excel Role','/v[\\d]+/role/excel','Role','GET',1, 1);
