--updateFor: 0-read, 1-update
--typ: 0-class, 1-field, 2-method,URL
create alias roleId AS $$
long id(int updateFor, String objName, int typ) {
  return biz.softfor.util.security.RoleCalc.id(updateFor != 0, objName, typ);
}
$$;

create table roles (
  id bigint not null
, defaultAccess tinyint not null default 0 --0-ALL, 1-AUTHENTICATED, 2-NOBODY
, isUrl tinyint not null default 0
, updateFor tinyint not null default 0 --0-read, 1-update
, disabled tinyint not null default 0 --0-enabled, 1-disabled
, orphan tinyint not null default 0 --0-not orphan, 1-orphan
, deniedForAll tinyint not null default 0
, isType tinyint not null default 0 --0-member/field/method, 1-type/class/parent
, name varchar(127) not null
, description varchar(511) not null
, objName varchar(1023) not null
);
alter table roles add constraint pk_roles primary key(id);
alter table roles add constraint u_roles_objName_updateFor unique (objName, updateFor);
--defaultAccess: 0-ALL, 1-AUTHENTICATED, 2-NOBODY
--updateFor: 0-read, 1-update
--typ: 0-class, 1-field, 2-method,URL
insert into roles(defaultAccess, isUrl, disabled, orphan, updateFor, objName, id, name, description) values
 (0,0,0,0,0,'biz.softfor.user.jpa.Role',roleId(0,'biz.softfor.user.jpa.Role',0),'Roles','Roles')
,(0,0,0,0,0,'biz.softfor.user.jpa.User',roleId(0,'biz.softfor.user.jpa.User',0),'Users','Users')
,(0,0,0,0,0,'biz.softfor.user.jpa.UserGroup',roleId(0,'biz.softfor.user.jpa.UserGroup',0),'User Groups','User Groups')
;

create table userGroups (
  id smallint not null generated by default as identity
, name varchar(63) not null
);
alter table userGroups add constraint pk_userGroups primary key(id);
insert into userGroups(name) values ('ADMIN');--1

create table roles_groups (
  roleId bigint not null
, groupId smallint not null
);
alter table roles_groups add constraint pk_roles_groups primary key(roleId, groupId);
alter table roles_groups add constraint fk_roles_groups_roles foreign key(roleId) references roles(id);
alter table roles_groups add constraint fk_roles_groups_userGroups foreign key(groupId) references userGroups(id);
insert into roles_groups(groupId, roleId) values
 (1, (SELECT id from roles WHERE objName='biz.softfor.user.jpa.UserGroup' AND updateFor=0))
,(1, (SELECT id from roles WHERE objName='biz.softfor.user.jpa.Role' AND updateFor=0))
,(1, (SELECT id from roles WHERE objName='biz.softfor.user.jpa.User' AND updateFor=0))
;

create table users (
  id bigint not null generated by default as identity
, personId bigint default null
, username varchar(63) not null
, password varchar(60) not null
, email varchar(95) not null
);
alter table users add constraint pk_users primary key(id);
create unique index ix_users_email on users(email asc);

create table users_groups (
  userId bigint not null
, groupId smallint not null
);
alter table users_groups add constraint pk_users_groups primary key(userId, groupId);
alter table users_groups add constraint fk_users_groups_users foreign key(userId) references users(id);
alter table users_groups add constraint fk_users_groups_groups foreign key(groupId) references userGroups(id);

create table tokens (
  id bigint not null generated by default as identity
, userId bigint not null
, isRefresh tinyint not null default 0 --0-access, 1-refresh
, expired datetime not null
, groups varchar(2047) not null
);
alter table tokens add constraint pk_tokens primary key(id);
alter table tokens add constraint fk_tokens_users foreign key(userId) references users(id);
create unique index ix_tokens_expired on tokens(expired asc);
