/*
-- Query: SELECT * FROM MergeTool.directive where idcollection < 7
LIMIT 0, 1000

-- Date: 2015-02-25 11:25
*/
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (15,19,2,1,'Insert Templates','*','templatefull','','','','','','TemplateDB');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (16,20,3,1,'Insert Directives','*','directivefull','idtemplate = {idtemplate}','','','{and},{or},{coma}','{semi},{period}','TemplateDB');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (17,20,1,4,'and','','','','{and}','and','{and}','and','');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (18,20,1,4,'or','','','','{or}','or','{or}','or','');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (19,20,1,4,'coma','','','','{coma}',',','{coma}',',','');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (20,20,1,4,'semicolin','','','','{semi}',';','{semi}',';','');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (21,20,1,4,'period','','','','{period}','.','{period}','.','');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (22,20,1,5,'Require idtemplate','','','','{idtemplate}','','{idtemplate}','','');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (23,21,1,2,'get collection','*','collection','idcollection = {idcollection}','','','','','TemplateDB');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (24,21,1,3,'Get Special Replace Values','fromValue, toValue','reportReplace','idreport = \'1\'','','','','','TemplateDB');
INSERT INTO `dragonfly`.`directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`,`jndiSource`) VALUES (33,41,2,3,'Get Corporate Wide replace values','*','corporate','','','','','','testgenDB');
