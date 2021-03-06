-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user` ;

CREATE TABLE IF NOT EXISTS `user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(512) NOT NULL,
  `password` VARCHAR(32) NOT NULL,
  `fname` VARCHAR(128) NULL,
  `lname` VARCHAR(128) NULL,
  `create_date` DATETIME NOT NULL,
  `is_admin` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pending_user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `pending_user` ;

CREATE TABLE IF NOT EXISTS `pending_user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(512) NOT NULL,
  `password` VARCHAR(32) NOT NULL,
  `fname` VARCHAR(128) NULL,
  `lname` VARCHAR(128) NULL,
  `create_date` DATETIME NOT NULL,
  `position` TEXT NOT NULL,
  `reason` TEXT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `contact_message`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `contact_message` ;

CREATE TABLE IF NOT EXISTS `contact_message` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `fname` VARCHAR(128) NOT NULL,
  `lname` VARCHAR(128) NOT NULL,
  `email` VARCHAR(512) NOT NULL,
  `question` TEXT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `data_setting`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `data_setting` ;

CREATE TABLE IF NOT EXISTS `data_setting` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `pull_frequency` INT NOT NULL,
  `data_retention` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `analytic_setting`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `analytic_setting` ;

CREATE TABLE IF NOT EXISTS `analytic_setting` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `data_access` INT NOT NULL,
  `top_words` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `filterquery`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `filterquerymakes`;
DROP TABLE IF EXISTS `filterquerymodels`;
DROP TABLE IF EXISTS `filterqueryalternates`;

CREATE TABLE filterquerymakes(
  `filterid` INT NOT NULL,
  `makes` varchar(2048),
  PRIMARY KEY(filterid)
)
ENGINE = InnoDB;
CREATE TABLE filterquerymodels(
  `make` varchar(128) NOT NULL,
  `model` varchar(2048),
  PRIMARY KEY(make)
)
ENGINE = InnoDB;
CREATE TABLE filterqueryalternates(
  `make` varchar(128) NOT NULL,
  `alternates` varchar(2048),
  PRIMARY KEY(make) 
)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `data_setting`
-- -----------------------------------------------------
START TRANSACTION;
INSERT INTO `data_setting` (`id`, `pull_frequency`, `data_retention`) VALUES (1, 1, 6);

COMMIT;


-- -----------------------------------------------------
-- Data for table `filterquery`
-- -----------------------------------------------------
START TRANSACTION;
insert into filterquerymakes values(0, 'generalmotors,chevrolet,gmc,buick,cadillac');

insert into filterquerymodels values('chevrolet', 'cruze,sonic,spark,impala,malibu,camaro,corvette,ss,captiva,equinox,trax,traverse,tahoe,suburban,express,savana,avalanche,silverado,colorado');
insert into filterquerymodels values('buick', 'verano,lacrosse,regal,encore,enclave');
insert into filterquerymodels values('cadillac', 'cts,xts,ats,cts-v,srx,escalade');
insert into filterquerymodels values('gmc', 'terrain,acadia,yukon,sierra');

insert into filterqueryalternates values ('chevrolet', 'vette,chevy');
COMMIT;

