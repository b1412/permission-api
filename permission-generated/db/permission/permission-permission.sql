INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (1,0,'Index Permission','/v[\\d]+/permission','Permission','GET',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (2,0,'Create Permission','/v[\\d]+/permission','Permission','POST',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (3,0,'Read Permission','/v[\\d]+/permission/[\\d]+','Permission','GET',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (4,0,'Update Permission','/v[\\d]+/permission/[\\d]+','Permission','PUT',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (5,0,'Delete a Permission','/v[\\d]+/permission/[\\d]+','Permission','DELETE',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (6,0,'Delete all Permission','/v[\\d]+/permission/clear','Permission','DELETE',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (7,0,'Excel Permission','/v[\\d]+/permission/excel','Permission','GET',1, 1);
