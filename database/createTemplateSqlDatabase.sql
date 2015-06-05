-- IBM SqlDatabase DDL

-- -----------------------------------------------------
-- Table codesDtype
-- -----------------------------------------------------
CREATE TABLE codesDtype (
  code INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
  meaning VARCHAR(45) NULL DEFAULT '',
  PRIMARY KEY (code));

-- -----------------------------------------------------
-- Table codesTtype
-- -----------------------------------------------------
CREATE TABLE codesTtype (
  code INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
  meaning VARCHAR(45) NULL DEFAULT ' ',
  PRIMARY KEY (code));

-- -----------------------------------------------------
-- Table collection
-- -----------------------------------------------------
CREATE TABLE collection (
  idcollection INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
  name VARCHAR(45) NOT NULL DEFAULT '',
  tableName VARCHAR(45) NULL DEFAULT '',
  columnName VARCHAR(45) NULL DEFAULT '',
  sampleValue VARCHAR(45) NULL DEFAULT '',
  PRIMARY KEY (idcollection),
  CONSTRAINT name_UNIQUE UNIQUE (name));

-- -----------------------------------------------------
-- Table template
-- -----------------------------------------------------
CREATE TABLE  template (
  idtemplate INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
  idcollection INT NOT NULL,
  columnValue VARCHAR(45) NULL DEFAULT '',
  name VARCHAR(45) NOT NULL DEFAULT '',
  type INT NOT NULL,
  output VARCHAR(45) NULL DEFAULT '',
  description VARCHAR(45) NULL DEFAULT '',
  content VARCHAR(4096) NULL DEFAULT '',
  PRIMARY KEY (idtemplate),
  CONSTRAINT fk_template_codeGroup1
    FOREIGN KEY (idcollection)
    REFERENCES collection (idcollection)
    ON DELETE CASCADE,
  CONSTRAINT fk_template_codeType1
    FOREIGN KEY (type)
    REFERENCES codesTtype (code)
    ON DELETE CASCADE);

-- -----------------------------------------------------
-- Table directive
-- -----------------------------------------------------
CREATE TABLE  directive (
  iddirective INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
  idtemplate INT NOT NULL,
  idcollection INT NOT NULL,
  type INT NOT NULL,
  description VARCHAR(45) NULL DEFAULT '',
  jndiSource VARCHAR(16) NULL DEFAULT '',
  selectColumns VARCHAR(1024) NULL DEFAULT '',
  fromTables VARCHAR(1024) NULL DEFAULT '',
  whereCondition VARCHAR(1024) NULL DEFAULT '',
  fromValue VARCHAR(45) NULL DEFAULT '',
  toValue VARCHAR(45) NULL DEFAULT '',
  notLast VARCHAR(45) NULL,
  onlyLast VARCHAR(45) NULL,
  PRIMARY KEY (iddirective),
  CONSTRAINT fk_directive_codeDirective1
    FOREIGN KEY (type)
    REFERENCES codesDtype (code)
    ON DELETE CASCADE,
  CONSTRAINT fk_directive_collection1
    FOREIGN KEY (idcollection)
    REFERENCES collection (idcollection)
    ON DELETE CASCADE,
  CONSTRAINT fk_sqlDirective_template1
    FOREIGN KEY (idtemplate)
    REFERENCES template (idtemplate)
    ON DELETE CASCADE);

-- -----------------------------------------------------
-- Table report
-- -----------------------------------------------------
CREATE TABLE  report (
  idreport INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
  idtemplate INT NOT NULL,
  outputRoot VARCHAR(255) NOT NULL DEFAULT '',
  name VARCHAR(45) NULL,
  PRIMARY KEY (idreport),
  CONSTRAINT fk_report_template1
    FOREIGN KEY (idtemplate)
    REFERENCES template (idtemplate)
    ON DELETE NO ACTION);

-- -----------------------------------------------------
-- Table reportReplace
-- -----------------------------------------------------
CREATE TABLE  reportReplace (
  idreportReplace INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
  idreport INT NOT NULL,
  fromValue VARCHAR(255) NOT NULL DEFAULT '',
  toValue VARCHAR(255) NULL DEFAULT '',
  PRIMARY KEY (idreportReplace),
  CONSTRAINT UnqueFrom UNIQUE (idreport, fromValue),
  CONSTRAINT fk_reportReplace_report1
    FOREIGN KEY (idreport)
    REFERENCES report (idreport)
    ON DELETE CASCADE);

-- -----------------------------------------------------
-- View directivefull
-- -----------------------------------------------------
CREATE VIEW directivefull AS 
select directive.iddirective AS iddirective,
	directive.idtemplate AS idtemplate,
	directive.idcollection AS idcollection,
	directive.type AS type,
	directive.description AS description,
	directive.jndiSource AS jndiSource,
	directive.selectColumns AS selectColumns,
	directive.fromTables AS fromTables,
	directive.whereCondition AS whereCondition,
	directive.fromValue AS fromValue,
	directive.toValue AS toValue,
	directive.notLast AS notLast,
	directive.onlyLast AS onlyLast,
	collection.name AS collection,
	collection.tableName AS tableName,
	collection.columnName AS columnName,
	collection.sampleValue AS sampleValue,
	codesDtype.code AS code,
	codesDtype.meaning AS directiveType 
from (directive, collection, codesDtype) 
where (	(directive.idcollection = collection.idcollection) and 
		(directive.type = codesDtype.code));

-- -----------------------------------------------------
-- View templatefull
-- -----------------------------------------------------
CREATE VIEW templatefull AS 
select template.idtemplate AS idtemplate,
	template.idcollection AS idcollection,
	template.columnValue AS columnValue,
	template.name AS name,
	template.type AS type,
	template.output AS output,
	template.description AS description,
	template.content AS content,
	collection.name AS collectionName,
	collection.tableName AS tableName,
	collection.columnName AS columnName,
	codesTtype.code AS code,
	codesTtype.meaning AS typeName,
	collection.name || ':' || template.name || ':' || template.columnValue AS fullName 
from template, collection, codesTtype
where (	(template.idcollection = collection.idcollection) and 
		(template.type = codesTtype.code));

INSERT INTO codesTtype (code, meaning) VALUES 
(1, 'Text'),
(2, 'XML');
COMMIT;

INSERT INTO codesDtype (code, meaning) VALUES 
(1, 'Insert'),
(2, 'ReplaceRow'),
(3, 'ReplaceCol'),
(4, 'ReplaceVal'),
(5, 'Require');
COMMIT;

INSERT INTO collection (idcollection,name,tableName,columnName,sampleValue) VALUES (1,'root','','','');
INSERT INTO collection (idcollection,name,tableName,columnName,sampleValue) VALUES (2,'dragonFly','','','');
INSERT INTO collection (idcollection,name,tableName,columnName,sampleValue) VALUES (3,'dragonFlyDirective','directivefull','directiveType','');
COMMIT;

INSERT INTO template (idtemplate,idcollection,columnValue,name,type,output,description,content) VALUES (18,1,'','default',1,'','The default template ','DEFAULT TEMPLATE - \nThis template was used because the requested template was not found!\n{tkReplaceValues}');
INSERT INTO template (idtemplate,idcollection,columnValue,name,type,output,description,content) VALUES (19,2,'','testRoot',1,'','Root Testing Template','<!DOCTYPE html>\n<html>\n	<head>\n		<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />	\n		<title>Situation Generator</title>\n	</head>\n	<body>\n	<p>This is the Test report of Template Definitions</p>\n	<table border=\"1\" width=\"1000px\">\n		<tr>\n			<th>Collection</th>\n			<th>Name</th>\n			<th>Column Value</th>\n			<th>Type</th>\n		</tr>\n<tkBookmark name=\"template\"/>\n	</table>\n	</body>\n</html>');
INSERT INTO template (idtemplate,idcollection,columnValue,name,type,output,description,content) VALUES (20,2,'','template',1,'','Single Template Report','<tr>\n	<td>{collectionName}</td>\n	<td>{name}</td>\n	<td>{columnValue}</td>\n	<td>{typeName}</td>\n</tr>\n<tr>\n	<td></td>\n	<td colspan=\"3\">\n	<textarea cols=\"132\" rows=\"10\">{content}</textarea>\n	</td>\n</tr>\n<tr>\n	<td></td>\n	<td colspan=\"3\"><table border=\"1\">\n		<tr>\n			<th>Type</th>\n			<th colspan=\"3\">Info</th>\n		</tr>\n<tkBookmark name=\"directive\"/>\n	</table></td>\n</tr>');
INSERT INTO template (idtemplate,idcollection,columnValue,name,type,output,description,content) VALUES (21,3,'','directive',1,'','Default Directive Report Template','<tr>\n	<td>{directiveType}</td>\n	<td colspan=\"3\">SELECT {selectColumns} FROM {fromTables} WHERE {whereCondition}</td>\n</tr>');
INSERT INTO template (idtemplate,idcollection,columnValue,name,type,output,description,content) VALUES (22,3,'Insert','directive',1,'','Insert Directive Report','<tr>\n	<td>{directiveType}</td>\n	<td>FROM {selectColumns} FROM {fromTables} WHERE {whereCondition}</td>\n	<td>Not Last Tags: {notLast}</td>\n	<td>Only Last Tags: {onlyLast}</td>\n</tr>');
INSERT INTO template (idtemplate,idcollection,columnValue,name,type,output,description,content) VALUES (23,3,'ReplaceVal','directive',1,'','ReplaceVal Directive Report','<tr>\n	<td>{directiveType}</td>\n	<td colspan=\"3\">Replace {fromValue} with {toValue}</td>\n</tr>');
COMMIT;

INSERT INTO directive (iddirective,idtemplate,idcollection,type,description,jndiSource,selectColumns,fromTables,whereCondition,fromValue,toValue,notLast,onlyLast) VALUES (15,19,2,1,'Insert Templates','dragonflyDB','*','templatefull','','','','','');
INSERT INTO directive (iddirective,idtemplate,idcollection,type,description,jndiSource,selectColumns,fromTables,whereCondition,fromValue,toValue,notLast,onlyLast) VALUES (16,20,3,1,'Insert Directives','dragonflyDB','*','directivefull','idtemplate = {idtemplate}','','','{and},{or},{coma}','{semi},{period}');
INSERT INTO directive (iddirective,idtemplate,idcollection,type,description,jndiSource,selectColumns,fromTables,whereCondition,fromValue,toValue,notLast,onlyLast) VALUES (17,20,1,4,'and','','','','','{and}','and','{and}','and');
INSERT INTO directive (iddirective,idtemplate,idcollection,type,description,jndiSource,selectColumns,fromTables,whereCondition,fromValue,toValue,notLast,onlyLast) VALUES (18,20,1,4,'or','','','','','{or}','or','{or}','or');
INSERT INTO directive (iddirective,idtemplate,idcollection,type,description,jndiSource,selectColumns,fromTables,whereCondition,fromValue,toValue,notLast,onlyLast) VALUES (19,20,1,4,'coma','','','','','{coma}',',','{coma}',',');
INSERT INTO directive (iddirective,idtemplate,idcollection,type,description,jndiSource,selectColumns,fromTables,whereCondition,fromValue,toValue,notLast,onlyLast) VALUES (20,20,1,4,'semicolin','','','','','{semi}',';','{semi}',';');
INSERT INTO directive (iddirective,idtemplate,idcollection,type,description,jndiSource,selectColumns,fromTables,whereCondition,fromValue,toValue,notLast,onlyLast) VALUES (21,20,1,4,'period','','','','','{period}','.','{period}','.');
INSERT INTO directive (iddirective,idtemplate,idcollection,type,description,jndiSource,selectColumns,fromTables,whereCondition,fromValue,toValue,notLast,onlyLast) VALUES (22,20,1,5,'Require idtemplate','','','','','{idtemplate}','','{idtemplate}','');
INSERT INTO directive (iddirective,idtemplate,idcollection,type,description,jndiSource,selectColumns,fromTables,whereCondition,fromValue,toValue,notLast,onlyLast) VALUES (23,21,1,2,'get collection','dragonflyDB','*','collection','idcollection = {idcollection}','','','','');
INSERT INTO directive (iddirective,idtemplate,idcollection,type,description,jndiSource,selectColumns,fromTables,whereCondition,fromValue,toValue,notLast,onlyLast) VALUES (24,21,1,3,'Get Special Replace Values','dragonflyDB','fromValue, toValue','reportReplace','idreport = \'1\'','','','','');
COMMIT;

INSERT INTO report (idreport,idtemplate,outputRoot,name) VALUES (1,19,'','test');
COMMIT;

INSERT INTO reportReplace (idreportReplace,idreport,fromValue,toValue) VALUES (1,1,'CacheReset','Yes');
COMMIT;
