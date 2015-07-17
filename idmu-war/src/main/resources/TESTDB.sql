-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema testgen
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema testgen
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS testgen;
CREATE SCHEMA IF NOT EXISTS `testgen` DEFAULT CHARACTER SET utf8 ;
USE `testgen` ;

-- -----------------------------------------------------
-- Table `testgen`.`CONTACT`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `testgen`.`CONTACT` ( `IDCONTACT` INT(11) NOT NULL, `IDCUSTOMER` INT(11) NOT NULL, `NAME` VARCHAR(45) NULL DEFAULT NULL, `PREFERENCE` VARCHAR(45) NULL DEFAULT NULL, `EMAIL` VARCHAR(45) NULL DEFAULT NULL, `PHONE` VARCHAR(45) NULL DEFAULT NULL);

-- -----------------------------------------------------
-- Table `testgen`.`CORPORATE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `testgen`.`CORPORATE` ( `IDCORPORATE` INT(11) NOT NULL, `FROM_VALUE` VARCHAR(45) NULL DEFAULT NULL, `TO_VALUE` VARCHAR(45) NULL DEFAULT NULL);

-- -----------------------------------------------------
-- Table `testgen`.`CUSTOMER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `testgen`.`CUSTOMER` ( `IDCUSTOMER` INT(11) NOT NULL, `PRIMARY` VARCHAR(45) NULL DEFAULT NULL, `NAME` VARCHAR(45) NULL DEFAULT NULL, `REVENUE` INT(11) NULL DEFAULT NULL, `PROFIT` INT(11) NULL DEFAULT NULL, `STREET` VARCHAR(95) NULL DEFAULT NULL, `CITY` VARCHAR(45) NULL DEFAULT NULL, `STATE` VARCHAR(45) NULL DEFAULT NULL, `ZIP` VARCHAR(45) NULL DEFAULT NULL, `PHONE` VARCHAR(45) NULL DEFAULT NULL);

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
commit;

-- Query: SELECT * FROM testgen.CORPORATE
-- LIMIT 0, 1000
-- Date: 2015-06-25 18:14
INSERT INTO `CORPORATE` (`IDCORPORATE`,`FROM_VALUE`,`TO_VALUE`) VALUES (1,'corpUrl','www.spacely.com');
INSERT INTO `CORPORATE` (`IDCORPORATE`,`FROM_VALUE`,`TO_VALUE`) VALUES (2,'corpStreet','101 Future Ave.');
INSERT INTO `CORPORATE` (`IDCORPORATE`,`FROM_VALUE`,`TO_VALUE`) VALUES (3,'corpCity','Space City');
INSERT INTO `CORPORATE` (`IDCORPORATE`,`FROM_VALUE`,`TO_VALUE`) VALUES (4,'corpState','IS');
INSERT INTO `CORPORATE` (`IDCORPORATE`,`FROM_VALUE`,`TO_VALUE`) VALUES (5,'corpZip','99353');
commit;

-- Query: SELECT * FROM testgen.CUSTOMER
-- LIMIT 0, 1000
-- Date: 2015-06-25 18:14
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (1,'James','General Motors',9824,806,'5791 Pleasant Prairie End','Dysart','PA','16188-0761','(878) 179-6603');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (2,'Robert','Exxon Mobil',5661,585,'1309 Burning Trail','Yazoo City','NE','68970-0108','(531) 984-8463');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (3,'John','U.S. Steel',3250,195,'3930 Iron Walk','Wild Horse','MO','65453-3393','(417) 591-1188');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (4,'Michael','General Electric',2959,213,'4363 Stony Crescent','Ste. Rose du Lac','CA','92186-9180','(650) 151-3276');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (5,'David','Esmark',2511,19,'9443 Colonial Berry Canyon','Fleming','DE','19753-5942','(302) 030-2953');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (6,'William','Chrysler',2072,19,'9386 Easy Vista','Wall City','MO','65266-8602','(660) 918-4630');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (7,'Richard','Armour',2056,2,'532 Umber Lake Highlands','Englefeld','RI','02956-5619','(401) 002-1176');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (8,'Thomas','Gulf Oil',1705,183,'4220 Rocky Wynd','Bolivia','MN','56253-0991','(952) 991-4731');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (9,'Charles','Mobil',1704,184,'5016 High Fawn Path','Boissevain','OK','73763-3294','(539) 389-7200');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (10,'Gary','DuPont',1688,344,'7151 Indian Limits','Mock City','NE','69497-1440','(308) 186-7323');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (11,'Larry','Amoco',1667,133,'8109 Quaking Extension','Freeze Fork','RI','02823-7971','(401) 842-8870');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (12,'Ronald','Bethlehem Steel',1660,117,'9717 Round Drive','Hardshell','OK','73108-1789','(918) 523-7811');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (13,'Joseph','CBS',1631,85,'8245 Lazy Cloud Crest','Treetops','MO','65194-7190','(636) 739-9890');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (14,'Donald','Texaco',1574,226,'2820 Amber Acres','Maple Creek','WI','54112-5496','(534) 839-0049');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (15,'Kenneth','AT&T Technologies',1526,56,'5414 Hidden Byway','Beersville','HI','96732-5653','(808) 216-1044');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (16,'Steven','Shell Oil',1312,121,'2638 Lost Robin Alley','Warroad','MO','64001-0474','(417) 976-4146');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (17,'Dennis','Kraft',1210,37,'2789 Silent Vale','Charcoal Landing','MO','64075-9452','(636) 650-5989');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (18,'Paul','ChevronTexaco',1113,212,'342 Blue Key','Zealandia','MN','56268-4891','(952) 107-5495');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (19,'Stephen','Goodyear Tire & Rubber',1090,48,'5479 Silver Villas','Effort','MN','56108-2359','(763) 274-6998');
INSERT INTO `CUSTOMER` (`IDCUSTOMER`,`PRIMARY`,`NAME`,`REVENUE`,`PROFIT`,`STREET`,`CITY`,`STATE`,`ZIP`,`PHONE`) VALUES (20,'George','Boeing',1033,37,'8680 Velvet Avenue','Sceptre','WI','54252-7174','(608) 776-1393');
commit;

-- Query: SELECT * FROM testgen.CONTACT
-- LIMIT 0, 1000
-- Date: 2015-06-25 18:14
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (1,1,'James','paper','James@General.com','(878) 555-0211');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (2,2,'Robert','email','Robert@Exxon.com','(531) 555-0422');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (3,3,'John','SMS','','(417) 555-0633');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (4,4,'Michael','paper','Michael@General.com','(650) 555-0844');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (5,5,'David','email','David@Esmark.com','(302) 555-1055');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (6,6,'William','SMS','William@Chrysler.com','(660) 555-1266');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (7,7,'Richard','paper','Richard@Armour.com','(401) 555-1477');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (8,8,'Thomas','email','Thomas@Gulf.com','(952) 555-1688');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (9,9,'Charles','SMS','Charles@Mobil.com','(539) 555-1899');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (10,10,'Gary','paper','Gary@DuPont.com','(308) 555-2010');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (11,11,'Larry','email','Larry@Amoco.com','(401) 555-2211');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (12,12,'Ronald','SMS','Ronald@Bethlehem.com','(918) 555-2412');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (13,13,'Joseph','paper','Joseph@CBS.com','(636) 555-2613');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (14,14,'Donald','email','Donald@Texaco.com','(534) 555-2814');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (15,15,'Kenneth','SMS','Kenneth@ATT.com','(808) 555-3015');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (16,16,'Steven','paper','Steven@Shell.com','(417) 555-3216');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (17,17,'Dennis','email','Dennis@Kraft.com','(636) 555-3417');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (18,18,'Paul','SMS','Paul@Chevron.com','(952) 555-3618');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (19,19,'Stephen','paper','Stephen@Goodyear.com','(763) 555-3819');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (20,20,'George','email','George@Boeing.com','(608) 555-4020');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (21,1,'Daniel','SMS','','(878) 555-2221');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (22,2,'Edward','paper','Edward@Exxon.com','(531) 555-2422');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (23,3,'Mark','email','Mark@USSteal.com','(417) 555-2623');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (24,4,'Jerry','SMS','Jerry@General.com','(650) 555-2824');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (25,5,'Gregory','paper','Gregory@Esmark.com','(302) 555-3025');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (26,6,'Bruce','email','Bruce@Chrysler.com','(660) 555-3226');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (27,7,'Roger','SMS','Roger@Armour.com','(401) 555-3427');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (28,8,'Douglas','paper','Douglas@Gulf.com','(952) 555-3628');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (29,9,'Frank','email','Frank@Mobil.com','(539) 555-3829');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (30,10,'Terry','SMS','Terry@DuPont.com','(308) 555-4030');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (31,11,'Raymond','paper','Raymond@Amoco.com','(401) 555-4231');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (32,12,'Timothy','email','Timothy@Bethlehem.com','(918) 555-4432');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (33,13,'Lawrence','SMS','Lawrence@CBS.com','(636) 555-4633');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (34,14,'Gerald','paper','Gerald@Texaco.com','(534) 555-4834');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (35,15,'Wayne','email','Wayne@ATT.com','(808) 555-5035');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (36,16,'Anthony','SMS','Anthony@Shell.com','(417) 555-5236');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (37,17,'Peter','paper','Peter@Kraft.com','(636) 555-5437');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (38,18,'Patrick','email','Patrick@Chevron.com','(952) 555-5638');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (39,19,'Danny','SMS','Danny@Goodyear.com','(763) 555-5839');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (40,20,'Walter','paper','Walter@Boeing.com','(608) 555-6040');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (41,1,'Alan','email','Alan@General.com','');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (42,2,'Willie','SMS','','(531) 555-4442');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (43,3,'Jeffrey','paper','Jeffrey@USSteal.com','(417) 555-4643');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (44,4,'Carl','email','Carl@General.com','(650) 555-4844');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (45,5,'Harold','SMS','Harold@Esmark.com','(302) 555-5045');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (46,6,'Arthur','paper','Arthur@Chrysler.com','(660) 555-5246');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (47,7,'Henry','email','Henry@Armour.com','(401) 555-5447');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (48,8,'Jack','SMS','Jack@Gulf.com','(952) 555-5648');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (49,9,'Dale','paper','Dale@Mobil.com','(539) 555-5849');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (50,10,'Johnny','email','Johnny@DuPont.com','(308) 555-6050');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (51,11,'Roy','SMS','Roy@Amoco.com','(401) 555-6251');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (52,12,'Ralph','paper','Ralph@Bethlehem.com','(918) 555-6452');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (53,13,'Phillipe','email','Phillip@CBS.com','(636) 555-6653');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (54,14,'Joe','SMS','Joe@Texaco.com','(534) 555-6854');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (55,15,'Albert','paper','Albert@ATT.com','');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (56,16,'Jimmy','email','Jimmy@Shell.com','(417) 555-7256');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (57,17,'Billy','SMS','Billy@Kraft.com','(636) 555-7457');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (58,18,'Eugene','paper','Eugene@Chevron.com','(952) 555-7658');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (59,19,'Glenn','email','Glenn@Goodyear.com','(763) 555-7859');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (60,20,'Stanley','SMS','Stanley@Boeing.com','(608) 555-8060');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (61,1,'Harry','paper','Harry@General.com','(878) 555-6261');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (62,2,'Samuel','email','Samuel@Exxon.com','(531) 555-6462');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (63,3,'Howard','SMS','Howard@USSteal.com','(417) 555-6663');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (64,4,'Phillip','paper','Phillip@General.com','(650) 555-6864');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (65,5,'Bobby','email','Bobby@Esmark.com','(302) 555-7065');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (66,6,'Christopher','SMS','Christopher@Chrysler.com','(660) 555-7266');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (67,7,'Louis','paper','Louis@Armour.com','(401) 555-7467');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (68,8,'Andrew','email','Andrew@Gulf.com','(952) 555-7668');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (69,9,'Russell','SMS','Russell@Mobil.com','(539) 555-7869');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (70,10,'Craig','paper','Craig@DuPont.com','(308) 555-8070');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (71,11,'Randall','email','Randall@Amoco.com','(401) 555-8271');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (72,12,'Allen','SMS','Allen@Bethlehem.com','(918) 555-8472');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (73,13,'Kevin','paper','Kevin@CBS.com','(636) 555-8673');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (74,14,'Barry','email','Barry@Texaco.com','(534) 555-8874');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (75,15,'Frederick','SMS','Frederick@ATT.com','(808) 555-9075');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (76,16,'Ronnie','paper','Ronnie@Shell.com','(417) 555-9276');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (77,17,'Leonard','email','Leonard@Kraft.com','(636) 555-9477');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (78,18,'Keith','SMS','Keith@Chevron.com','(952) 555-9678');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (79,19,'Brian','paper','Brian@Goodyear.com','(763) 555-9879');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (80,20,'Randy','email','Randy@Boeing.com','(608) 555-0080');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (81,1,'Ernest','SMS','Ernest@General.com','(878) 555-8281');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (82,2,'Scott','paper','Scott@Exxon.com','(531) 555-8482');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (83,3,'Fred','email','Fred@USSteal.com','(417) 555-8683');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (84,4,'Steve','SMS','Steve@General.com','(650) 555-8884');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (85,5,'Martin','paper','Martin@Esmark.com','(302) 555-9085');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (86,6,'Francis','email','Francis@Chrysler.com','(660) 555-9286');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (87,7,'Melvin','SMS','Melvin@Armour.com','(401) 555-9487');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (88,8,'Rodney','paper','Rodney@Gulf.com','(952) 555-9688');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (89,9,'Eddie','email','Eddie@Mobil.com','(539) 555-9889');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (90,10,'Norman','SMS','Norman@DuPont.com','(308) 555-0090');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (91,11,'Lee','paper','Lee@Amoco.com','(401) 555-0291');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (92,12,'Earl','email','Earl@Bethlehem.com','(918) 555-0492');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (93,13,'Marvin','SMS','Marvin@CBS.com','(636) 555-0693');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (94,14,'Tommy','paper','Tommy@Texaco.com','(534) 555-0894');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (95,15,'Clarence','email','Clarence@ATT.com','(808) 555-1095');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (96,16,'Alfred','SMS','Alfred@Shell.com','(417) 555-1296');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (97,17,'Curtis','paper','Curtis@Kraft.com','(636) 555-1497');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (98,18,'Eric','email','Eric@Chevron.com','(952) 555-1698');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (99,19,'Theodore','SMS','Theodore@Goodyear.com','(763) 555-1899');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (100,20,'Clifford','paper','Clifford@Boeing.com','(608) 555-2010');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (101,1,'Linda','email','Linda@General.com','(878) 555-0211');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (102,2,'Mary','SMS','Mary@Exxon.com','(531) 555-0412');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (103,3,'Patricia','paper','Patricia@USSteal.com','(417) 555-0613');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (104,4,'Barbara','email','Barbara@General.com','(650) 555-0814');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (105,5,'Susan','SMS','Susan@Esmark.com','(302) 555-1015');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (106,6,'Nancy','paper','Nancy@Chrysler.com','(660) 555-1216');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (107,7,'Deborah','email','Deborah@Armour.com','(401) 555-1417');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (108,8,'Sandra','SMS','Sandra@Gulf.com','(952) 555-1618');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (109,9,'Carol','paper','Carol@Mobil.com','(539) 555-1819');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (110,10,'Kathleen','email','Kathleen@DuPont.com','(308) 555-2010');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (111,11,'Sharon','SMS','Sharon@Amoco.com','(401) 555-2211');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (112,12,'Karen','paper','Karen@Bethlehem.com','(918) 555-2412');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (113,13,'Donna','email','Donna@CBS.com','(636) 555-2613');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (114,14,'Brenda','SMS','Brenda@Texaco.com','(534) 555-2814');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (115,15,'Margaret','paper','Margaret@ATT.com','(808) 555-3015');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (116,16,'Diane','email','Diane@Shell.com','(417) 555-3216');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (117,17,'Pamela','SMS','Pamela@Kraft.com','(636) 555-3417');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (118,18,'Janet','paper','Janet@Chevron.com','(952) 555-3618');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (119,19,'Shirley','email','Shirley@Goodyear.com','(763) 555-3819');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (120,20,'Carolyn','SMS','Carolyn@Boeing.com','(608) 555-4010');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (121,1,'Judith','paper','Judith@General.com','(878) 555-2211');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (122,2,'Janice','email','Janice@Exxon.com','(531) 555-2412');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (123,3,'Cynthia','SMS','Cynthia@USSteal.com','(417) 555-2613');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (124,4,'Elizabeth','paper','Elizabeth@General.com','(650) 555-2814');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (125,5,'Judy','email','Judy@Esmark.com','(302) 555-3015');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (126,6,'Betty','SMS','Betty@Chrysler.com','(660) 555-3216');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (127,7,'Joyce','paper','Joyce@Armour.com','(401) 555-3417');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (128,8,'Christine','email','Christine@Gulf.com','(952) 555-3618');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (129,9,'Cheryl','SMS','Cheryl@Mobil.com','(539) 555-3819');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (130,10,'Gloria','paper','Gloria@DuPont.com','(308) 555-4010');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (131,11,'Beverly','email','Beverly@Amoco.com','(401) 555-4211');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (132,12,'Martha','SMS','Martha@Bethlehem.com','(918) 555-4412');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (133,13,'Bonnie','paper','Bonnie@CBS.com','(636) 555-4613');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (134,14,'Catherine','email','Catherine@Texaco.com','(534) 555-4814');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (135,15,'Dorothy','SMS','Dorothy@ATT.com','(808) 555-5015');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (136,16,'Rebecca','paper','Rebecca@Shell.com','(417) 555-5216');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (137,17,'Marilyn','email','Marilyn@Kraft.com','(636) 555-5417');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (138,18,'Kathy','SMS','Kathy@Chevron.com','(952) 555-5618');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (139,19,'Jane','paper','Jane@Goodyear.com','(763) 555-5819');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (140,20,'Joan','email','Joan@Boeing.com','(608) 555-6010');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (141,1,'Peggy','SMS','Peggy@General.com','(878) 555-4211');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (142,2,'Gail','paper','Gail@Exxon.com','(531) 555-4412');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (143,3,'Virginia','email','Virginia@USSteal.com','(417) 555-4613');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (144,4,'Connie','SMS','Connie@General.com','(650) 555-4814');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (145,5,'Ann','paper','Ann@Esmark.com','(302) 555-5015');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (146,6,'Kathryn','email','Kathryn@Chrysler.com','(660) 555-5216');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (147,7,'Diana','SMS','Diana@Armour.com','(401) 555-5417');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (148,8,'Jean','paper','Jean@Gulf.com','(952) 555-5618');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (149,9,'Ruth','email','Ruth@Mobil.com','(539) 555-5819');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (150,10,'Helen','SMS','Helen@DuPont.com','(308) 555-6010');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (151,11,'Frances','paper','Frances@Amoco.com','(401) 555-6211');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (152,12,'Wanda','email','Wanda@Bethlehem.com','(918) 555-6412');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (153,13,'Phyllis','SMS','Phyllis@CBS.com','(636) 555-6613');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (154,14,'Paula','paper','Paula@Texaco.com','(534) 555-6814');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (155,15,'Jacqueline','email','Jacqueline@ATT.com','(808) 555-7015');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (156,16,'Rita','SMS','Rita@Shell.com','(417) 555-7216');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (157,17,'Alice','paper','Alice@Kraft.com','(636) 555-7417');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (158,18,'Katherine','email','Katherine@Chevron.com','(952) 555-7618');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (159,19,'Debra','SMS','Debra@Goodyear.com','(763) 555-7819');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (160,20,'Elaine','paper','Elaine@Boeing.com','(608) 555-8010');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (161,1,'Vicki','email','Vicki@General.com','(878) 555-6211');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (162,2,'Sherry','SMS','Sherry@Exxon.com','(531) 555-6412');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (163,3,'Laura','paper','Laura@USSteal.com','(417) 555-6613');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (164,4,'Jo','email','Jo@General.com','(650) 555-6814');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (165,5,'Theresa','SMS','Theresa@Esmark.com','(302) 555-7015');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (166,6,'Ellen','paper','Ellen@Chrysler.com','(660) 555-7216');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (167,7,'Joanne','email','Joanne@Armour.com','(401) 555-7417');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (168,8,'Marsha','SMS','Marsha@Gulf.com','(952) 555-7618');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (169,9,'Rose','paper','Rose@Mobil.com','(539) 555-7819');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (170,10,'Sheila','email','Sheila@DuPont.com','(308) 555-8010');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (171,11,'Suzanne','SMS','Suzanne@Amoco.com','(401) 555-8211');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (172,12,'Marie','paper','Marie@Bethlehem.com','(918) 555-8412');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (173,13,'Maria','email','Maria@CBS.com','(636) 555-8613');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (174,14,'Doris','SMS','Doris@Texaco.com','(534) 555-8814');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (175,15,'Cathy','paper','Cathy@ATT.com','(808) 555-9015');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (176,16,'Lynn','email','Lynn@Shell.com','(417) 555-9216');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (177,17,'Marcia','SMS','Marcia@Kraft.com','(636) 555-9417');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (178,18,'Sally','paper','Sally@Chevron.com','(952) 555-9618');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (179,19,'Darlene','email','Darlene@Goodyear.com','(763) 555-9819');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (180,20,'Charlotte','SMS','Charlotte@Boeing.com','(608) 555-0010');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (181,1,'Teresa','paper','Teresa@General.com','(878) 555-8211');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (182,2,'Denise','email','Denise@Exxon.com','(531) 555-8412');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (183,3,'Lois','SMS','Lois@USSteal.com','(417) 555-8613');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (184,4,'Anne','paper','Anne@General.com','(650) 555-8814');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (185,5,'Constance','email','Constance@Esmark.com','(302) 555-9015');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (186,6,'Evelyn','SMS','Evelyn@Chrysler.com','(660) 555-9216');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (187,7,'Glenda','paper','Glenda@Armour.com','(401) 555-9417');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (188,8,'Sarah','email','Sarah@Gulf.com','(952) 555-9618');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (189,9,'Maureen','SMS','Maureen@Mobil.com','(539) 555-9819');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (190,10,'Dianne','paper','Dianne@DuPont.com','(308) 555-0010');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (191,11,'Eileen','email','Eileen@Amoco.com','(401) 555-0211');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (192,12,'Irene','SMS','Irene@Bethlehem.com','(918) 555-0412');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (193,13,'Anna','paper','Anna@CBS.com','(636) 555-0613');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (194,14,'Victoria','email','Victoria@Texaco.com','(534) 555-0814');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (195,15,'Jeanne','SMS','Jeanne@ATT.com','(808) 555-1015');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (196,16,'Roberta','paper','Roberta@Shell.com','(417) 555-1216');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (197,17,'Sylvia','email','Sylvia@Kraft.com','(636) 555-1417');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (198,18,'Joann','SMS','Joann@Chevron.com','(952) 555-1618');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (199,19,'Anita','paper','Anita@Goodyear.com','(763) 555-1819');
INSERT INTO `CONTACT` (`IDCONTACT`,`IDCUSTOMER`,`NAME`,`PREFERENCE`,`EMAIL`,`PHONE`) VALUES (200,20,'Sue','email','Sue@Boeing.com','(608) 555-2020');
commit;

