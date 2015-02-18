INSERT INTO `MergeTool`.`codesTtype` (`code`, `meaning`) VALUES 
(1, 'Text'),
(2, 'XML');
COMMIT;

INSERT INTO `MergeTool`.`codesDtype` (`code`, `meaning`) VALUES 
(1, 'Insert'),
(2, 'ReplaceRow'),
(3, 'ReplaceCol'),
(4, 'ReplaceVal'),
(5, 'Require');
COMMIT;

INSERT INTO `MergeTool`.`collection` (`idcollection`, `name`, `tableName`, `columnName`, `sampleValue`) VALUES 
(1,'root','','',''),
(2,'test','','',''),
(3,'testDirective','directiveFull','directiveType',''),
(4,'Sample','','',''),
(5,'Sample.Type','contacts','prefer','');
COMMIT;
