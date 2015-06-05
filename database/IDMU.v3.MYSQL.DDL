-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema 
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema IDMU
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema IDMU
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `IDMU` DEFAULT CHARACTER SET utf8 ;
-- -----------------------------------------------------
-- Schema 
-- -----------------------------------------------------
USE `IDMU` ;

-- -----------------------------------------------------
-- Table `IDMU`.`DIRECTIVE_TYPE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `IDMU`.`DIRECTIVE_TYPE` (
  `TYPE` INT(11) NOT NULL AUTO_INCREMENT,
  `MEANING` VARCHAR(45) NULL DEFAULT '',
  PRIMARY KEY (`TYPE`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `IDMU`.`TEMPLATE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `IDMU`.`TEMPLATE` (
  `ID_TEMPLATE` INT(11) NOT NULL AUTO_INCREMENT,
  `COLLECTION` VARCHAR(45) NOT NULL,
  `NAME` VARCHAR(45) NOT NULL DEFAULT '',
  `COLUMN_VALUE` VARCHAR(45) NOT NULL DEFAULT '',
  `OUTPUT` VARCHAR(45) NULL DEFAULT '',
  `DESCRIPTION` VARCHAR(45) NULL DEFAULT '',
  `CONTENT` VARCHAR(4096) NULL DEFAULT '',
  PRIMARY KEY (`ID_TEMPLATE`),
  UNIQUE INDEX `UNIQUE` (`COLLECTION` ASC, `NAME` ASC, `COLUMN_VALUE` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 29
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `IDMU`.`DIRECTIVE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `IDMU`.`DIRECTIVE` (
  `ID_TEMPLATE` INT(11) NOT NULL,
  `SEQUENCE` INT(11) NOT NULL,
  `TYPE` INT(11) NOT NULL,
  `DESCRIPTION` VARCHAR(45) NULL DEFAULT '',
  `SOFT_FAIL` CHAR(1) NOT NULL DEFAULT 'N',
  `DIR_1` VARCHAR(1024) NULL DEFAULT NULL,
  `DIR_2` VARCHAR(1024) NULL DEFAULT NULL,
  `DIR_3` VARCHAR(1024) NULL DEFAULT NULL,
  `DIR_4` VARCHAR(1024) NULL DEFAULT NULL,
  `PRO_1` VARCHAR(1024) NULL DEFAULT NULL,
  `PRO_2` VARCHAR(1024) NULL DEFAULT NULL,
  `PRO_3` VARCHAR(1024) NULL DEFAULT NULL,
  `PRO_4` VARCHAR(4096) NULL DEFAULT NULL,
  PRIMARY KEY (`ID_TEMPLATE`, `SEQUENCE`),
  INDEX `fk_directive_codeDirective1_idx` (`TYPE` ASC),
  INDEX `fk_sqlDirective_template1` (`ID_TEMPLATE` ASC),
  CONSTRAINT `fk_directive_codeDirective1`
    FOREIGN KEY (`TYPE`)
    REFERENCES `IDMU`.`DIRECTIVE_TYPE` (`TYPE`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_sqlDirective_template1`
    FOREIGN KEY (`ID_TEMPLATE`)
    REFERENCES `IDMU`.`TEMPLATE` (`ID_TEMPLATE`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 25
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
