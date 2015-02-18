-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema MergeTool
-- -----------------------------------------------------
DROP SCHEMA `MergeTool`;

-- -----------------------------------------------------
-- Schema MergeTool
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `MergeTool` DEFAULT CHARACTER SET utf8 ;
USE `MergeTool` ;


-- -----------------------------------------------------
-- Table `MergeTool`.`codesDtype`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `MergeTool`.`codesDtype` (
  `code` INT(11) NOT NULL AUTO_INCREMENT,
  `meaning` VARCHAR(45) NULL DEFAULT '',
  PRIMARY KEY (`code`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8;



-- -----------------------------------------------------
-- Table `MergeTool`.`codesTtype`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `MergeTool`.`codesTtype` (
  `code` INT(11) NOT NULL AUTO_INCREMENT,
  `meaning` VARCHAR(45) NULL DEFAULT ' ',
  PRIMARY KEY (`code`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `MergeTool`.`collection`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `MergeTool`.`collection` (
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
-- Table `MergeTool`.`template`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `MergeTool`.`template` (
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
    REFERENCES `MergeTool`.`collection` (`idcollection`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_template_codeType1`
    FOREIGN KEY (`type`)
    REFERENCES `MergeTool`.`codesTtype` (`code`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 14
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `MergeTool`.`directive`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `MergeTool`.`directive` (
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
    REFERENCES `MergeTool`.`codesDtype` (`code`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_directive_collection1`
    FOREIGN KEY (`idcollection`)
    REFERENCES `MergeTool`.`collection` (`idcollection`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_sqlDirective_template1`
    FOREIGN KEY (`idtemplate`)
    REFERENCES `MergeTool`.`template` (`idtemplate`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8;



-- -----------------------------------------------------
-- Table `MergeTool`.`report`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `MergeTool`.`report` (
  `idreport` INT(11) NOT NULL AUTO_INCREMENT,
  `idtemplate` INT(11) NOT NULL,
  `outputRoot` VARCHAR(255) NOT NULL DEFAULT '',
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`idreport`),
  INDEX `fk_report_template1_idx` (`idtemplate` ASC),
  CONSTRAINT `fk_report_template1`
    FOREIGN KEY (`idtemplate`)
    REFERENCES `MergeTool`.`template` (`idtemplate`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `MergeTool`.`reportReplace`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `MergeTool`.`reportReplace` (
  `idreportReplace` INT NOT NULL AUTO_INCREMENT,
  `idreport` INT(11) NOT NULL,
  `fromValue` VARCHAR(255) NOT NULL DEFAULT '',
  `toValue` VARCHAR(255) NULL DEFAULT '',
  PRIMARY KEY (`idreportReplace`),
  UNIQUE INDEX `UnqueFrom` (`idreport` ASC, `fromValue` ASC),
  CONSTRAINT `fk_reportReplace_report1`
    FOREIGN KEY (`idreport`)
    REFERENCES `MergeTool`.`report` (`idreport`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

USE `MergeTool` ;
-- -----------------------------------------------------
-- View `MergeTool`.`directivefull`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `MergeTool`.`directivefull`;
USE `MergeTool`;
CREATE VIEW `MergeTool`.`directivefull` AS 
select `MergeTool`.`directive`.`iddirective` AS `iddirective`,
	`MergeTool`.`directive`.`idtemplate` AS `idtemplate`,
	`MergeTool`.`directive`.`idcollection` AS `idcollection`,
	`MergeTool`.`directive`.`type` AS `type`,
	`MergeTool`.`directive`.`description` AS `description`,
	`MergeTool`.`directive`.`selectColumns` AS `selectColumns`,
	`MergeTool`.`directive`.`fromTables` AS `fromTables`,
	`MergeTool`.`directive`.`whereCondition` AS `whereCondition`,
	`MergeTool`.`directive`.`fromValue` AS `fromValue`,
	`MergeTool`.`directive`.`toValue` AS `toValue`,
	`MergeTool`.`directive`.`notLast` AS `notLast`,
	`MergeTool`.`directive`.`onlyLast` AS `onlyLast`,
	`MergeTool`.`collection`.`name` AS `collection`,
	`MergeTool`.`collection`.`tableName` AS `tableName`,
	`MergeTool`.`collection`.`columnName` AS `columnName`,
	`MergeTool`.`collection`.`sampleValue` AS `sampleValue`,
	`MergeTool`.`codesDtype`.`code` AS `code`,
	`MergeTool`.`codesDtype`.`meaning` AS `directiveType` 
from ((`MergeTool`.`directive` join `MergeTool`.`collection`) join `MergeTool`.`codesDtype`) 
	where ((`MergeTool`.`directive`.`idcollection` = `MergeTool`.`collection`.`idcollection`) and 
		(`MergeTool`.`directive`.`type` = `MergeTool`.`codesDtype`.`code`));

-- -----------------------------------------------------
-- View `MergeTool`.`templatefull`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `MergeTool`.`templatefull`;
USE `MergeTool`;
CREATE VIEW `MergeTool`.`templatefull` AS 
select `MergeTool`.`template`.`idtemplate` AS `idtemplate`,
	`MergeTool`.`template`.`idcollection` AS `idcollection`,
	`MergeTool`.`template`.`columnValue` AS `columnValue`,
	`MergeTool`.`template`.`name` AS `name`,
	`MergeTool`.`template`.`type` AS `type`,
	`MergeTool`.`template`.`output` AS `output`,
	`MergeTool`.`template`.`description` AS `description`,
	`MergeTool`.`template`.`content` AS `content`,
	`MergeTool`.`collection`.`name` AS `collectionName`,
	`MergeTool`.`collection`.`tableName` AS `tableName`,
	`MergeTool`.`collection`.`columnName` AS `columnName`,
	`MergeTool`.`codesTtype`.`code` AS `code`,
	`MergeTool`.`codesTtype`.`meaning` AS `typeName`,
	concat(`MergeTool`.`collection`.`name`,':',`MergeTool`.`template`.`name`,':',`MergeTool`.`template`.`columnValue`) AS `fullName` 
from ((`MergeTool`.`template` join `MergeTool`.`collection`) join `MergeTool`.`codesTtype`) 
	where ((`MergeTool`.`template`.`idcollection` = `MergeTool`.`collection`.`idcollection`) and 
		(`MergeTool`.`template`.`type` = `MergeTool`.`codesTtype`.`code`));

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


