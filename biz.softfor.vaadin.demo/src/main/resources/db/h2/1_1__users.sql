--defaultAccess: 0-ALL, 1-AUTHENTICATED, 2-NOBODY
--updateFor: 0-read, 1-update
--typ: 0-class, 1-field, 2-method,URL
insert into roles(defaultAccess, isUrl, disabled, orphan, updateFor, objName, id, name, description) values
 (0,0,0,0,0,'biz.softfor.address.jpa.City',roleId(0,'biz.softfor.address.jpa.City',0),'City','biz.softfor.address.jpa City')
,(0,0,0,0,1,'biz.softfor.address.jpa.City',roleId(1,'biz.softfor.address.jpa.City',0),'City (update)','biz.softfor.address.jpa City (update)')
,(0,0,0,0,0,'biz.softfor.address.jpa.Country',roleId(0,'biz.softfor.address.jpa.Country',0),'Country','biz.softfor.address.jpa Country')
,(0,0,0,0,1,'biz.softfor.address.jpa.Country',roleId(1,'biz.softfor.address.jpa.Country',0),'Country (update)','biz.softfor.address.jpa Country (update)')
,(0,0,0,0,0,'biz.softfor.address.jpa.State',roleId(0,'biz.softfor.address.jpa.State',0),'State','biz.softfor.address.jpa State')
,(0,0,0,0,1,'biz.softfor.address.jpa.State',roleId(1,'biz.softfor.address.jpa.State',0),'State (update)','biz.softfor.address.jpa State (update)')
,(0,0,0,0,0,'biz.softfor.address.jpa.State.name',roleId(0,'biz.softfor.address.jpa.State.name',1),'State#name','biz.softfor.address.jpa State#name')
,(0,0,0,0,1,'biz.softfor.address.jpa.State.name',roleId(1,'biz.softfor.address.jpa.State.name',1),'State#name (update)','biz.softfor.address.jpa State#name (update)')
,(0,0,0,0,0,'biz.softfor.partner.jpa.Partner.partnerName',roleId(0,'biz.softfor.partner.jpa.Partner.partnerName',1),'Partner#partnerName','biz.softfor.partner.jpa Partner#partnerName')
,(0,0,0,0,1,'biz.softfor.partner.jpa.Partner.partnerName',roleId(1,'biz.softfor.partner.jpa.Partner.partnerName',1),'Partner#partnerName (update)','biz.softfor.partner.jpa Partner#partnerName (update)')
,(0,0,0,0,0,'biz.softfor.partner.jpa.Partner.personDetails',roleId(0,'biz.softfor.partner.jpa.Partner.personDetails',1),'Partner#person details','biz.softfor.partner.jpa Partner#person details')
,(0,0,0,0,1,'biz.softfor.partner.jpa.Partner.personDetails',roleId(1,'biz.softfor.partner.jpa.Partner.personDetails',1),'Partner#person details (update)','biz.softfor.partner.jpa Partner#person details (update)')
,(0,0,0,0,0,'biz.softfor.partner.jpa.Partner.locationType',roleId(0,'biz.softfor.partner.jpa.Partner.locationType',1),'Partner#location type','biz.softfor.partner.jpa Partner#location type')
,(0,0,0,0,1,'biz.softfor.partner.jpa.Partner.locationType',roleId(1,'biz.softfor.partner.jpa.Partner.locationType',1),'Partner#location type (update)','biz.softfor.partner.jpa Partner#location type (update)')
;
insert into userGroups(name) values
 ('MANAGER')--2
,('USER')--3
,('NO_USERS_NO_ROLES')--4
,('CITIES_EDITORS')--5
,('COUNTRIES_EDITORS')--6
;
insert into roles_groups(groupId, roleId) values
--CITIES_EDITORS
 (5, (SELECT id from roles WHERE objName='biz.softfor.address.jpa.City' AND updateFor=0))
,(5, (SELECT id from roles WHERE objName='biz.softfor.address.jpa.City' AND updateFor=1))
--COUNTRIES_EDITORS
,(6, (SELECT id from roles WHERE objName='biz.softfor.address.jpa.Country' AND updateFor=0))
,(6, (SELECT id from roles WHERE objName='biz.softfor.address.jpa.Country' AND updateFor=1))
;
insert into users(email, username, password) values
 ('admin@t.co','admin','$2a$10$hCeQz1XCjby4gYv0AWHzduI1lj93LMu1tqJoJunQrgHCvG02sE9ZO')--1 admin
,('manager@t.co','manager','$2a$10$w.brX2W5rVsfAFC70JFvS.HUtRJxdbaiceDOrzP9wuy3sWVWloUni')--2 manager
,('user@t.co','user','$2a$10$17/y/xb9x3IHJipB3v4K7O9zQzvDMN21U6VnDlgXam1sf3/3zE5B.')--3 user
;
insert into users_groups(userId, groupId) values
--admin -> ADMIN, USER
 (1, 1),(1, 3)--,(1, 5),(1, 6)
--manager -> MANAGER
,(2, 2)
--user -> USER
,(3, 3)
;
