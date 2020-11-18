INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (1,0,'Index Branch','/v[\\d]+/branch','Branch','GET',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (2,0,'Create Branch','/v[\\d]+/branch','Branch','POST',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (3,0,'Read Branch','/v[\\d]+/branch/[\\d]+','Branch','GET',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (4,0,'Update Branch','/v[\\d]+/branch/[\\d]+','Branch','PUT',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (5,0,'Delete a Branch','/v[\\d]+/branch/[\\d]+','Branch','DELETE',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (6,0,'Delete all Branch','/v[\\d]+/branch/clear','Branch','DELETE',1, 1);
INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
VALUES (7,0,'Excel Branch','/v[\\d]+/branch/excel','Branch','GET',1, 1);
