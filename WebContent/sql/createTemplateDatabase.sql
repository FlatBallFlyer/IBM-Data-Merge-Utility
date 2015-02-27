-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema dragonfly
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `dragonfly`;

-- -----------------------------------------------------
-- Schema dragonfly
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `dragonfly` DEFAULT CHARACTER SET utf8 ;
USE `dragonfly` ;


-- -----------------------------------------------------
-- Table `dragonfly`.`codesDtype`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dragonfly`.`codesDtype` (
  `code` INT(11) NOT NULL AUTO_INCREMENT,
  `meaning` VARCHAR(45) NULL DEFAULT '',
  PRIMARY KEY (`code`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8;



-- -----------------------------------------------------
-- Table `dragonfly`.`codesTtype`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dragonfly`.`codesTtype` (
  `code` INT(11) NOT NULL AUTO_INCREMENT,
  `meaning` VARCHAR(45) NULL DEFAULT ' ',
  PRIMARY KEY (`code`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dragonfly`.`collection`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dragonfly`.`collection` (
  `idcollection` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL DEFAULT '',
  `tableName` VARCHAR(45) NULL DEFAULT '',
  `columnName` VARCHAR(45) NULL DEFAULT '',
  `sampleValue` VARCHAR(45) NULL DEFAULT '',
  PRIMARY KEY (`idcollection`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;



-- -----------------------------------------------------
-- Table `dragonfly`.`template`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dragonfly`.`template` (
  `idtemplate` INT(11) NOT NULL AUTO_INCREMENT,
  `idcollection` INT(11) NOT NULL,
  `columnValue` VARCHAR(45) NULL DEFAULT '',
  `name` VARCHAR(45) NOT NULL DEFAULT '',
  `type` INT(11) NOT NULL,
  `output` VARCHAR(45) NULL DEFAULT '',
  `description` VARCHAR(45) NULL DEFAULT '',
  `content` VARCHAR(4096) NULL DEFAULT '',
  PRIMARY KEY (`idtemplate`),
  INDEX `fk_template_codeType1_idx` (`type` ASC),
  INDEX `fk_template_codeGroup1_idx` (`idcollection` ASC),
  CONSTRAINT `fk_template_codeGroup1`
    FOREIGN KEY (`idcollection`)
    REFERENCES `dragonfly`.`collection` (`idcollection`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_template_codeType1`
    FOREIGN KEY (`type`)
    REFERENCES `dragonfly`.`codesTtype` (`code`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 14
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dragonfly`.`directive`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dragonfly`.`directive` (
  `iddirective` INT(11) NOT NULL AUTO_INCREMENT,
  `idtemplate` INT(11) NOT NULL,
  `idcollection` INT(11) NOT NULL,
  `type` INT(11) NOT NULL,
  `description` VARCHAR(45) NULL DEFAULT '',
  `jndiSource` VARCHAR(16) NULL DEFAULT '',
  `selectColumns` VARCHAR(1024) NULL DEFAULT '',
  `fromTables` VARCHAR(1024) NULL DEFAULT '',
  `whereCondition` VARCHAR(1024) NULL DEFAULT '',
  `fromValue` VARCHAR(45) NULL DEFAULT '',
  `toValue` VARCHAR(45) NULL DEFAULT '',
  `notLast` VARCHAR(45) NULL,
  `onlyLast` VARCHAR(45) NULL,
  PRIMARY KEY (`iddirective`),
  INDEX `fk_directive_codeDirective1_idx` (`type` ASC),
  INDEX `fk_directive_collection1_idx` (`idcollection` ASC),
  INDEX `fk_sqlDirective_template1` (`idtemplate` ASC),
  CONSTRAINT `fk_directive_codeDirective1`
    FOREIGN KEY (`type`)
    REFERENCES `dragonfly`.`codesDtype` (`code`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_directive_collection1`
    FOREIGN KEY (`idcollection`)
    REFERENCES `dragonfly`.`collection` (`idcollection`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_sqlDirective_template1`
    FOREIGN KEY (`idtemplate`)
    REFERENCES `dragonfly`.`template` (`idtemplate`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8;



-- -----------------------------------------------------
-- Table `dragonfly`.`report`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dragonfly`.`report` (
  `idreport` INT(11) NOT NULL AUTO_INCREMENT,
  `idtemplate` INT(11) NOT NULL,
  `outputRoot` VARCHAR(255) NOT NULL DEFAULT '',
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`idreport`),
  INDEX `fk_report_template1_idx` (`idtemplate` ASC),
  CONSTRAINT `fk_report_template1`
    FOREIGN KEY (`idtemplate`)
    REFERENCES `dragonfly`.`template` (`idtemplate`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dragonfly`.`reportReplace`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dragonfly`.`reportReplace` (
  `idreportReplace` INT NOT NULL AUTO_INCREMENT,
  `idreport` INT(11) NOT NULL,
  `fromValue` VARCHAR(255) NOT NULL DEFAULT '',
  `toValue` VARCHAR(255) NULL DEFAULT '',
  PRIMARY KEY (`idreportReplace`),
  UNIQUE INDEX `UnqueFrom` (`idreport` ASC, `fromValue` ASC),
  CONSTRAINT `fk_reportReplace_report1`
    FOREIGN KEY (`idreport`)
    REFERENCES `dragonfly`.`report` (`idreport`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

USE `dragonfly` ;
-- -----------------------------------------------------
-- View `dragonfly`.`directivefull`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dragonfly`.`directivefull`;
USE `dragonfly`;
CREATE VIEW `dragonfly`.`directivefull` AS 
select `dragonfly`.`directive`.`iddirective` AS `iddirective`,
	`dragonfly`.`directive`.`idtemplate` AS `idtemplate`,
	`dragonfly`.`directive`.`idcollection` AS `idcollection`,
	`dragonfly`.`directive`.`type` AS `type`,
	`dragonfly`.`directive`.`description` AS `description`,
	`dragonfly`.`directive`.`jndiSource` AS `jndiSource`,
	`dragonfly`.`directive`.`selectColumns` AS `selectColumns`,
	`dragonfly`.`directive`.`fromTables` AS `fromTables`,
	`dragonfly`.`directive`.`whereCondition` AS `whereCondition`,
	`dragonfly`.`directive`.`fromValue` AS `fromValue`,
	`dragonfly`.`directive`.`toValue` AS `toValue`,
	`dragonfly`.`directive`.`notLast` AS `notLast`,
	`dragonfly`.`directive`.`onlyLast` AS `onlyLast`,
	`dragonfly`.`collection`.`name` AS `collection`,
	`dragonfly`.`collection`.`tableName` AS `tableName`,
	`dragonfly`.`collection`.`columnName` AS `columnName`,
	`dragonfly`.`collection`.`sampleValue` AS `sampleValue`,
	`dragonfly`.`codesDtype`.`code` AS `code`,
	`dragonfly`.`codesDtype`.`meaning` AS `directiveType` 
from ((`dragonfly`.`directive` join `dragonfly`.`collection`) join `dragonfly`.`codesDtype`) 
	where ((`dragonfly`.`directive`.`idcollection` = `dragonfly`.`collection`.`idcollection`) and 
		(`dragonfly`.`directive`.`type` = `dragonfly`.`codesDtype`.`code`));

-- -----------------------------------------------------
-- View `dragonfly`.`templatefull`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dragonfly`.`templatefull`;
USE `dragonfly`;
CREATE VIEW `dragonfly`.`templatefull` AS 
select `dragonfly`.`template`.`idtemplate` AS `idtemplate`,
	`dragonfly`.`template`.`idcollection` AS `idcollection`,
	`dragonfly`.`template`.`columnValue` AS `columnValue`,
	`dragonfly`.`template`.`name` AS `name`,
	`dragonfly`.`template`.`type` AS `type`,
	`dragonfly`.`template`.`output` AS `output`,
	`dragonfly`.`template`.`description` AS `description`,
	`dragonfly`.`template`.`content` AS `content`,
	`dragonfly`.`collection`.`name` AS `collectionName`,
	`dragonfly`.`collection`.`tableName` AS `tableName`,
	`dragonfly`.`collection`.`columnName` AS `columnName`,
	`dragonfly`.`codesTtype`.`code` AS `code`,
	`dragonfly`.`codesTtype`.`meaning` AS `typeName`,
	concat(`dragonfly`.`collection`.`name`,':',`dragonfly`.`template`.`name`,':',`dragonfly`.`template`.`columnValue`) AS `fullName` 
from ((`dragonfly`.`template` join `dragonfly`.`collection`) join `dragonfly`.`codesTtype`) 
	where ((`dragonfly`.`template`.`idcollection` = `dragonfly`.`collection`.`idcollection`) and 
		(`dragonfly`.`template`.`type` = `dragonfly`.`codesTtype`.`code`));

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

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
INSERT INTO `dragonfly`.`collection` (`idcollection`,`name`,`tableName`,`columnName`,`sampleValue`) VALUES (3,'dragonFlyDirective','directivefull','directiveType','');
COMMIT;

INSERT INTO `template` (`idtemplate`,`idcollection`,`columnValue`,`name`,`type`,`output`,`description`,`content`) VALUES (18,1,'','default',1,'','The default template ','DEFAULT TEMPLATE - \nThis template was used because the requested template was not found!\n{tkReplaceValues}');
INSERT INTO `template` (`idtemplate`,`idcollection`,`columnValue`,`name`,`type`,`output`,`description`,`content`) VALUES (19,2,'','testRoot',1,'','Root Testing Template','<!DOCTYPE html>\n<html>\n	<head>\n		<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />	\n		<title>Situation Generator</title>\n	</head>\n	<body>\n	<p>This is the Test report of Template Definitions</p>\n	<table border=\"1\" width=\"1000px\">\n		<tr>\n			<th>Collection</th>\n			<th>Name</th>\n			<th>Column Value</th>\n			<th>Type</th>\n		</tr>\n<tkBookmark name=\"template\"/>\n	</table>\n	</body>\n</html>');
INSERT INTO `template` (`idtemplate`,`idcollection`,`columnValue`,`name`,`type`,`output`,`description`,`content`) VALUES (20,2,'','template',1,'','Single Template Report','<tr>\n	<td>{collectionName}</td>\n	<td>{name}</td>\n	<td>{columnValue}</td>\n	<td>{typeName}</td>\n</tr>\n<tr>\n	<td></td>\n	<td colspan=\"3\">\n	<textarea cols=\"132\" rows=\"10\">{content}</textarea>\n	</td>\n</tr>\n<tr>\n	<td></td>\n	<td colspan=\"3\"><table border=\"1\">\n		<tr>\n			<th>Type</th>\n			<th colspan=\"3\">Info</th>\n		</tr>\n<tkBookmark name=\"directive\"/>\n	</table></td>\n</tr>');
INSERT INTO `template` (`idtemplate`,`idcollection`,`columnValue`,`name`,`type`,`output`,`description`,`content`) VALUES (21,3,'','directive',1,'','Default Directive Report Template','<tr>\n	<td>{directiveType}</td>\n	<td colspan=\"3\">SELECT {selectColumns} FROM {fromTables} WHERE {whereCondition}</td>\n</tr>');
INSERT INTO `template` (`idtemplate`,`idcollection`,`columnValue`,`name`,`type`,`output`,`description`,`content`) VALUES (22,3,'Insert','directive',1,'','Insert Directive Report','<tr>\n	<td>{directiveType}</td>\n	<td>FROM {selectColumns} FROM {fromTables} WHERE {whereCondition}</td>\n	<td>Not Last Tags: {notLast}</td>\n	<td>Only Last Tags: {onlyLast}</td>\n</tr>');
INSERT INTO `template` (`idtemplate`,`idcollection`,`columnValue`,`name`,`type`,`output`,`description`,`content`) VALUES (23,3,'ReplaceVal','directive',1,'','ReplaceVal Directive Report','<tr>\n	<td>{directiveType}</td>\n	<td colspan=\"3\">Replace {fromValue} with {toValue}</td>\n</tr>');
COMMIT;

INSERT INTO `directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`jndiSource`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`) VALUES (15,19,2,1,'Insert Templates','dragonflyDB','*','templatefull','','','','','');
INSERT INTO `directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`jndiSource`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`) VALUES (16,20,3,1,'Insert Directives','dragonflyDB','*','directivefull','idtemplate = {idtemplate}','','','{and},{or},{coma}','{semi},{period}');
INSERT INTO `directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`jndiSource`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`) VALUES (17,20,1,4,'and','','','','','{and}','and','{and}','and');
INSERT INTO `directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`jndiSource`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`) VALUES (18,20,1,4,'or','','','','','{or}','or','{or}','or');
INSERT INTO `directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`jndiSource`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`) VALUES (19,20,1,4,'coma','','','','','{coma}',',','{coma}',',');
INSERT INTO `directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`jndiSource`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`) VALUES (20,20,1,4,'semicolin','','','','','{semi}',';','{semi}',';');
INSERT INTO `directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`jndiSource`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`) VALUES (21,20,1,4,'period','','','','','{period}','.','{period}','.');
INSERT INTO `directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`jndiSource`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`) VALUES (22,20,1,5,'Require idtemplate','','','','','{idtemplate}','','{idtemplate}','');
INSERT INTO `directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`jndiSource`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`) VALUES (23,21,1,2,'get collection','dragonflyDB','*','collection','idcollection = {idcollection}','','','','');
INSERT INTO `directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`jndiSource`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`) VALUES (24,21,1,3,'Get Special Replace Values','dragonflyDB','fromValue, toValue','reportReplace','idreport = \'1\'','','','','');
INSERT INTO `directive` (`iddirective`,`idtemplate`,`idcollection`,`type`,`description`,`jndiSource`,`selectColumns`,`fromTables`,`whereCondition`,`fromValue`,`toValue`,`notLast`,`onlyLast`) VALUES (33,41,2,3,'Get Corporate Wide replace values','testgenDB','*','corporate','','','','','');
COMMIT;

INSERT INTO `dragonfly`.`report` (`idreport`,`idtemplate`,`outputRoot`,`name`) VALUES (1,19,'','test');
COMMIT;

INSERT INTO `dragonfly`.`reportReplace` (`idreportReplace`,`idreport`,`fromValue`,`toValue`) VALUES (1,1,'CacheReset','Yes');
COMMIT;
