-- IBM SqlDatabase DDL
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
  description VARCHAR(250) NULL DEFAULT '',
  content VARCHAR(4000) NULL DEFAULT '',
  PRIMARY KEY (idtemplate)
);

-- -----------------------------------------------------
-- Table directive
-- -----------------------------------------------------
CREATE TABLE  directive (
  iddirective INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
  idtemplate INT NOT NULL,
  idcollection INT NOT NULL,
  type INT NOT NULL,
  description VARCHAR(250) NULL DEFAULT '',
  jndiSource VARCHAR(16) NULL DEFAULT '',
  selectColumns VARCHAR(1024) NULL DEFAULT '',
  fromTables VARCHAR(1024) NULL DEFAULT '',
  whereCondition VARCHAR(1024) NULL DEFAULT '',
  fromValue VARCHAR(45) NULL DEFAULT '',
  toValue VARCHAR(45) NULL DEFAULT '',
  notLast VARCHAR(45) NULL,
  onlyLast VARCHAR(45) NULL,
  PRIMARY KEY (iddirective),
  CONSTRAINT fk_sqlDirective_template1
    FOREIGN KEY (idtemplate)
    REFERENCES template (idtemplate)
    ON DELETE CASCADE
);
