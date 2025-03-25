--typ: 0-person, 10-employee, 20-department, 30-legal entity, 31-organization
insert into partners(typ,locationTypeId,postcodeId,parentId,partnerName,partnerRegdate,partnerRegcode,address,partnerFullname) values
 (30,1,  989,null,'First Co','2020-02-22','111111111','First St., 1a','FIRST COMPANY, Ltd')--1
,(31,2,  989,   1,'Plimuth Tax Cons.','1920-12-05','000011111','Second Sq., 22-11','Plimuth Tax Consulting')--2
,( 0,1,  989,null,'John','1970-10-20','23456789','Third av., 33-44','Doe')--3
,(10,1,  989,   3,'Mike','1990-12-16','89234567','Third av., 33-44b','Big')--4
;
update users set personId=1 where id=1;
update users set personId=2 where id=2;
update users set personId=1 where id=3;
update users set personId=4 where id=4;

insert into partnerDetails(id,note) values
 (1,'FIRST COMPANY main office')
,(3,'John Doe''s home office')
,(4,'Mike Big''s address')
;
insert into personDetails(id,passportSeries,passportNumber,passportDate,passportIssued,middlename,married) values
 (3,'AA',111111,'2000-12-20','Issued by DW','Shpak',false)
,(4,'QQ',444444,'1980-05-11','Issued by XAD','Vailovych',true)
;
insert into partnerFiles(partnerId,descr,uri) values
 (3,'passport','1.png')
,(3,'invitation to the conference','2.png')
,(3,'master''s degree diploma','3.png')
,(4,'visa','4.jpg')
,(4,'referral to the clinic','5.jpg')
;
