INSERT INTO `dragonfly`.`codesTtype` (`code`, `meaning`) VALUES 
(1, 'Text'),
(2, 'XML');
COMMIT;

INSERT INTO `dragonfly`.`codesDtype` (`code`, `meaning`) VALUES 
(1, 'Insert'),
(2, 'ReplaceRow'),
(3, 'ReplaceCol'),
(4, 'ReplaceVal'),
(5, 'Require');
COMMIT;

INSERT INTO `dragonfly`.`collection` (`idcollection`,`name`,`tableName`,`columnName`,`sampleValue`) VALUES (1,'root','','','');
INSERT INTO `dragonfly`.`collection` (`idcollection`,`name`,`tableName`,`columnName`,`sampleValue`) VALUES (2,'dragonFly','','','');
INSERT INTO `dragonfly`.`collection` (`idcollection`,`name`,`tableName`,`columnName`,`sampleValue`) VALUES (3,'dragonFlyDirective','directiveFull','directiveType','');
COMMIT;
