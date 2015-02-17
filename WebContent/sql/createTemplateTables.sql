-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema tk
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema tk
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `tk` DEFAULT CHARACTER SET utf8 ;
USE `tk` ;


-- -----------------------------------------------------
-- Table `tk`.`codesDtype`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tk`.`codesDtype` (
  `code` INT(11) NOT NULL AUTO_INCREMENT,
  `meaning` VARCHAR(45) NULL DEFAULT '',
  PRIMARY KEY (`code`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8;



-- -----------------------------------------------------
-- Table `tk`.`codesTtype`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tk`.`codesTtype` (
  `code` INT(11) NOT NULL AUTO_INCREMENT,
  `meaning` VARCHAR(45) NULL DEFAULT ' ',
  PRIMARY KEY (`code`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tk`.`collection`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tk`.`collection` (
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
-- Table `tk`.`template`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tk`.`template` (
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
    REFERENCES `tk`.`collection` (`idcollection`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_template_codeType1`
    FOREIGN KEY (`type`)
    REFERENCES `tk`.`codesTtype` (`code`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 14
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tk`.`directive`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tk`.`directive` (
  `iddirective` INT(11) NOT NULL AUTO_INCREMENT,
  `idtemplate` INT(11) NOT NULL,
  `idcollection` INT(11) NOT NULL,
  `type` INT(11) NOT NULL,
  `description` VARCHAR(45) NULL DEFAULT '',
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
    REFERENCES `tk`.`codesDtype` (`code`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_directive_collection1`
    FOREIGN KEY (`idcollection`)
    REFERENCES `tk`.`collection` (`idcollection`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_sqlDirective_template1`
    FOREIGN KEY (`idtemplate`)
    REFERENCES `tk`.`template` (`idtemplate`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8;



-- -----------------------------------------------------
-- Table `tk`.`report`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tk`.`report` (
  `idreport` INT(11) NOT NULL AUTO_INCREMENT,
  `idtemplate` INT(11) NOT NULL,
  `outputRoot` VARCHAR(255) NOT NULL DEFAULT '',
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`idreport`),
  INDEX `fk_report_template1_idx` (`idtemplate` ASC),
  CONSTRAINT `fk_report_template1`
    FOREIGN KEY (`idtemplate`)
    REFERENCES `tk`.`template` (`idtemplate`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tk`.`reportReplace`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tk`.`reportReplace` (
  `idreportReplace` INT NOT NULL AUTO_INCREMENT,
  `idreport` INT(11) NOT NULL,
  `fromValue` VARCHAR(255) NOT NULL DEFAULT '',
  `toValue` VARCHAR(255) NULL DEFAULT '',
  PRIMARY KEY (`idreportReplace`),
  UNIQUE INDEX `UnqueFrom` (`idreport` ASC, `fromValue` ASC),
  CONSTRAINT `fk_reportReplace_report1`
    FOREIGN KEY (`idreport`)
    REFERENCES `tk`.`report` (`idreport`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

USE `tk` ;
-- -----------------------------------------------------
-- View `tk`.`directivefull`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tk`.`directivefull`;
USE `tk`;
CREATE VIEW `tk`.`directivefull` AS 
select `tk`.`directive`.`iddirective` AS `iddirective`,
	`tk`.`directive`.`idtemplate` AS `idtemplate`,
	`tk`.`directive`.`idcollection` AS `idcollection`,
	`tk`.`directive`.`type` AS `type`,
	`tk`.`directive`.`selectColumns` AS `selectColumns`,
	`tk`.`directive`.`fromTables` AS `fromTables`,
	`tk`.`directive`.`whereCondition` AS `whereCondition`,
	`tk`.`directive`.`fromValue` AS `fromValue`,
	`tk`.`directive`.`toValue` AS `toValue`,
	`tk`.`directive`.`notLast` AS `notLast`,
	`tk`.`directive`.`onlyLast` AS `onlyLast`,
	`tk`.`collection`.`name` AS `collection`,
	`tk`.`collection`.`tableName` AS `tableName`,
	`tk`.`collection`.`columnName` AS `columnName`,
	`tk`.`collection`.`sampleValue` AS `sampleValue`,
	`tk`.`codesdtype`.`code` AS `code`,
	`tk`.`codesdtype`.`meaning` AS `directiveType` 
from ((`tk`.`directive` join `tk`.`collection`) join `tk`.`codesdtype`) 
	where ((`tk`.`directive`.`idcollection` = `tk`.`collection`.`idcollection`) and 
		(`tk`.`directive`.`type` = `tk`.`codesdtype`.`code`));

-- -----------------------------------------------------
-- View `tk`.`templatefull`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tk`.`templatefull`;
USE `tk`;
CREATE VIEW `tk`.`templatefull` AS 
select `tk`.`template`.`idtemplate` AS `idtemplate`,
	`tk`.`template`.`idcollection` AS `idcollection`,
	`tk`.`template`.`columnValue` AS `columnValue`,
	`tk`.`template`.`name` AS `name`,
	`tk`.`template`.`type` AS `type`,
	`tk`.`template`.`output` AS `output`,
	`tk`.`template`.`description` AS `description`,
	`tk`.`template`.`content` AS `content`,
	`tk`.`collection`.`name` AS `collectionName`,
	`tk`.`collection`.`tableName` AS `tableName`,
	`tk`.`collection`.`columnName` AS `columnName`,
	`tk`.`codesttype`.`code` AS `code`,
	`tk`.`codesttype`.`meaning` AS `typeName`,
	concat(`tk`.`collection`.`name`,':',`tk`.`template`.`name`,':',`tk`.`template`.`columnValue`) AS `fullName` 
from ((`tk`.`template` join `tk`.`collection`) join `tk`.`codesttype`) 
	where ((`tk`.`template`.`idcollection` = `tk`.`collection`.`idcollection`) and 
		(`tk`.`template`.`type` = `tk`.`codesttype`.`code`));

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
