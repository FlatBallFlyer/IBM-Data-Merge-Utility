DROP SCHEMA IF EXISTS IDMU;
CREATE SCHEMA IDMU DEFAULT CHARACTER SET utf8 ;
USE IDMU ;

-- -----------------------------------------------------
-- Table IDMU_TEMPLATE
-- -----------------------------------------------------
CREATE TABLE IDMU_TEMPLATE (
  COLLECTION VARCHAR(45) NOT NULL,
  TEMPLATE_NAME VARCHAR(45) NOT NULL DEFAULT '',
  COLUMN_VALUE VARCHAR(45) NOT NULL DEFAULT '',
  OUTPUT_FILE VARCHAR(45) NULL DEFAULT '',
  DESCRIPTION VARCHAR(45) NULL DEFAULT '',
  CONTENT VARCHAR(4000) NULL DEFAULT '',
  PRIMARY KEY (COLLECTION, TEMPLATE_NAME, COLUMN_VALUE),
  UNIQUE INDEX UNIQUE_NAME (COLLECTION ASC, TEMPLATE_NAME ASC, COLUMN_VALUE ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table IDMU_DIRECTIVE
-- -----------------------------------------------------
CREATE TABLE IDMU_DIRECTIVE (
  COLLECTION VARCHAR(45) NOT NULL,
  TEMPLATE_NAME VARCHAR(45) NOT NULL DEFAULT '',
  COLUMN_VALUE VARCHAR(45) NOT NULL DEFAULT '',
  SEQUENCE INT(11) NOT NULL,
  DIR_TYPE INT(11) NOT NULL,
  DESCRIPTION VARCHAR(45) NULL DEFAULT '',
  SOFT_FAIL CHAR(1) NOT NULL DEFAULT 'N',
  DIR_1 VARCHAR(1024) NULL DEFAULT NULL,
  DIR_2 VARCHAR(1024) NULL DEFAULT NULL,
  DIR_3 VARCHAR(1024) NULL DEFAULT NULL,
  DIR_4 VARCHAR(1024) NULL DEFAULT NULL,
  PRO_1 VARCHAR(1024) NULL DEFAULT NULL,
  PRO_2 VARCHAR(1024) NULL DEFAULT NULL,
  PRO_3 VARCHAR(1024) NULL DEFAULT NULL,
  PRO_4 VARCHAR(4000) NULL DEFAULT NULL,
  PRIMARY KEY (COLLECTION, TEMPLATE_NAME, COLUMN_VALUE, SEQUENCE),
  INDEX fk_sqlDirective_template1 (COLLECTION ASC, TEMPLATE_NAME ASC, COLUMN_VALUE ASC),
  CONSTRAINT fk_DIRECTIVE_TEMPLATE
    FOREIGN KEY (COLLECTION , TEMPLATE_NAME , COLUMN_VALUE)
    REFERENCES IDMU_TEMPLATE (COLLECTION , TEMPLATE_NAME , COLUMN_VALUE)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

INSERT INTO IDMU_TEMPLATE (COLLECTION, TEMPLATE_NAME, COLUMN_VALUE, CONTENT) values ("system", "default", "", "This is the Default Template");