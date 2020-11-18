INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (1,0,'Index User','/v[\\d]+/user','User','GET',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (2,0,'Create User','/v[\\d]+/user','User','POST',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (3,0,'Read User','/v[\\d]+/user/[\\d]+','User','GET',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (4,0,'Update User','/v[\\d]+/user/[\\d]+','User','PUT',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (5,0,'Delete a User','/v[\\d]+/user/[\\d]+','User','DELETE',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (6,0,'Delete all User','/v[\\d]+/user/clear','User','DELETE',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (7,0,'Excel User','/v[\\d]+/user/excel','User','GET',1, 1);
