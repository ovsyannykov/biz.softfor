insert into userGroups(name) values
 ('MANAGER')--2
;
insert into roles(defaultAccess, isUrl, orphan, updateFor, objName, id, name, description) values
 (0,0,0,0,'biz.softfor.address.jpa.Country.name',roleId(0,'biz.softfor.address.jpa.Country.name',1),'Country#name','biz.softfor.address.jpa.Country#name')
,(0,0,0,0,'biz.softfor.address.jpa.Country.fullname',roleId(0,'biz.softfor.address.jpa.Country.fullname',1),'Country#fullname','biz.softfor.address.jpa.Country#fullname')
,(0,0,0,0,'biz.softfor.address.jpa.State.name',roleId(0,'biz.softfor.address.jpa.State.name',1),'State#name','biz.softfor.address.jpa.State#name')
,(0,0,0,0,'biz.softfor.address.jpa.State.fullname',roleId(0,'biz.softfor.address.jpa.State.fullname',1),'State#fullname','biz.softfor.address.jpa.State#fullname')
,(0,0,0,0,'biz.softfor.address.jpa.District.fullname',roleId(0,'biz.softfor.address.jpa.District.fullname',1),'District#fullname','biz.softfor.address.jpa.District#fullname')
;
insert into roles_groups(groupId, roleId) values
 (1,(SELECT id from roles WHERE objName='biz.softfor.address.jpa.Country.name' AND updateFor=0))
,(1,(SELECT id from roles WHERE objName='biz.softfor.address.jpa.Country.fullname' AND updateFor=0))
,(1,(SELECT id from roles WHERE objName='biz.softfor.address.jpa.State.name' AND updateFor=0))
,(1,(SELECT id from roles WHERE objName='biz.softfor.address.jpa.State.fullname' AND updateFor=0))
,(1,(SELECT id from roles WHERE objName='biz.softfor.address.jpa.District.fullname' AND updateFor=0))
;
