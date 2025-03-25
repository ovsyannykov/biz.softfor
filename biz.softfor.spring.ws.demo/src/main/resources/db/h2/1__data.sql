insert into userGroups(id, name) values
  (41, 'ROLE_ADMIN')
, (42, 'ROLE_USER')
, (43, 'ROLE_MODER')
, (44, 'ROLE_SOMETHING')
, (45, 'ROLE_SOMETH4')
, (46, 'ROLE_SOMETH5')
, (47, 'ROLE_SOMETH6')
, (48, 'ROLE_SOMETH7')
;
insert into users(id, personId, username, password, email) values
  (51, null, 'sadm', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'sadm@gmail.com')
, (52, null, 'user', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'user@gmail.com')
, (53, null, 'moder', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'moder@t.co')
, (54, null, 'user_wo_role', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'usrwor@gmail.com')
;
insert into users_groups(userId, groupId) values
  (51, 41)
, (51, 42)
, (53, 43)
;
