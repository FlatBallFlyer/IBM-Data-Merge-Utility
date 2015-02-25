/*
-- Query: SELECT * FROM MergeTool.directive where idcollection in (7,8)
LIMIT 0, 1000

-- Date: 2015-02-25 11:22
*/
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (28,34,7,1,'Insert Customers','*','customer','','','','','','testgenDB');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (29,35,8,1,'Insert Contacts','*','contact','idcustomer = {idcustomer} ORDER BY name','','','','','testgenDB');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (30,43,7,1,'Insert SMS Contacts','*','contact','preference = \'SMS\'','','','','','testgenDB');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (32,41,7,1,'Single Row Insert to build 2 reports','\'foo\' as bar','','','','','','','testgenDB');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (34,46,7,1,'Create the output file','\'foo\' as bar','','','','','','','testgenDB');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (35,45,7,1,'Insert eMail Contacts','*','contact','preference = \'eMail\'','','','','','testgenDB');
