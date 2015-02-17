INSERT INTO `tk`.`codesTtype` (`code`, `meaning`) VALUES 
(1, 'Text'),
(2, 'XML');
COMMIT;

INSERT INTO `tk`.`codesDtype` (`code`, `meaning`) VALUES 
(1, 'Insert'),
(2, 'ReplaceRow'),
(3, 'ReplaceCol'),
(4, 'ReplaceVal'),
(5, 'Require');
COMMIT;

INSERT INTO `collection` (`idcollection`, `name`, `tableName`, `columnName`, `sampleValue`) VALUES 
(1,'root','','',''),
(2,'test','','','');
(2,'testDirective','directiveFull','directiveType','');
COMMIT;
