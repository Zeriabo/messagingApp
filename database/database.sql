-- messaging database

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema messaging
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `messaging` ;

-- -----------------------------------------------------
-- Schema messaging
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `messaging` DEFAULT CHARACTER SET utf8mb4 ;
USE `messaging` ;

-- -----------------------------------------------------
-- Table `messaging`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `messaging`.`users` (
  `idusers` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `dateofbirth` DATE NULL DEFAULT NULL,
  `secretquestion` VARCHAR(100) NOT NULL,
  `secretanswer` VARCHAR(100) NOT NULL,
  `active` TINYINT(4) NULL DEFAULT 0,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`idusers`))
ENGINE = InnoDB
AUTO_INCREMENT = 23
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `messaging`.`authentication`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `messaging`.`authentication` (
  `id` INT(11) NOT NULL,
  `token` VARCHAR(100) NOT NULL,
  `users_idusers` INT(11) NOT NULL,
  `created_at` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_authentication_users1_idx` (`users_idusers` ASC) VISIBLE,
  CONSTRAINT `fk_authentication_users1`
    FOREIGN KEY (`users_idusers`)
    REFERENCES `messaging`.`users` (`idusers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `messaging`.`messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `messaging`.`messages` (
  `idmessages` INT(11) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NOT NULL,
  `messagebody` BLOB NOT NULL,
  `created_at` VARCHAR(45) NOT NULL,
  `nbrofrecipients` INT(11) NOT NULL,
  `idsender` INT(11) NOT NULL,
  PRIMARY KEY (`idmessages`, `idsender`),
  INDEX `fk_messages_users1_idx` (`idsender` ASC) VISIBLE,
  CONSTRAINT `fk_messages_users1`
    FOREIGN KEY (`idsender`)
    REFERENCES `messaging`.`users` (`idusers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 18
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `messaging`.`receiver`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `messaging`.`receiver` (
  `users_idusers` INT(11) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `messages_idmessages` INT(11) NOT NULL,
  `messages_idsender` INT(11) NOT NULL,
  PRIMARY KEY (`messages_idmessages`, `messages_idsender`),
  INDEX `fk_receiver_users1_idx` (`users_idusers` ASC) VISIBLE,
  CONSTRAINT `fk_receiver_messages1`
    FOREIGN KEY (`messages_idmessages` , `messages_idsender`)
    REFERENCES `messaging`.`messages` (`idmessages` , `idsender`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_receiver_users1`
    FOREIGN KEY (`users_idusers`)
    REFERENCES `messaging`.`users` (`idusers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `messaging`.`secret_keys`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `messaging`.`secret_keys` (
  `idsecret_keys` INT(11) NOT NULL AUTO_INCREMENT,
  `secret_key` BLOB NULL DEFAULT NULL,
  `messages_idmessages` INT(11) NOT NULL,
  `messages_idsender` INT(11) NOT NULL,
  `created_at` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`idsecret_keys`),
  INDEX `fk_secret_keys_messages1_idx` (`messages_idmessages` ASC, `messages_idsender` ASC) VISIBLE,
  CONSTRAINT `fk_secret_keys_messages1`
    FOREIGN KEY (`messages_idmessages` , `messages_idsender`)
    REFERENCES `messaging`.`messages` (`idmessages` , `idsender`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 18
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `messaging`.`sender`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `messaging`.`sender` (
  `users_idusers` INT(11) NOT NULL,
  `created_at` DATETIME NOT NULL,
  INDEX `fk_sender_users_idx` (`users_idusers` ASC) VISIBLE,
  CONSTRAINT `fk_sender_users`
    FOREIGN KEY (`users_idusers`)
    REFERENCES `messaging`.`users` (`idusers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `messaging`.`user_requests`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `messaging`.`user_requests` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `dateofbirth` DATE NULL DEFAULT NULL,
  `secretquestion` VARCHAR(100) NOT NULL,
  `secretanswer` VARCHAR(100) NOT NULL,
  `creationdate` DATETIME NULL DEFAULT NULL,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 16
DEFAULT CHARACTER SET = utf8mb4;

USE `messaging`;

DELIMITER $$
USE `messaging`$$
CREATE
DEFINER=`root`@`%`
TRIGGER `messaging`.`user_created_timestamp`
BEFORE INSERT ON `messaging`.`users`
FOR EACH ROW
SET NEW.created_at = NOW()$$

USE `messaging`$$
CREATE
DEFINER=`root`@`%`
TRIGGER `messaging`.`user_updated_timestamp`
BEFORE UPDATE ON `messaging`.`users`
FOR EACH ROW
SET NEW.updated_at = NOW()$$

USE `messaging`$$
CREATE
DEFINER=`root`@`%`
TRIGGER `messaging`.`authentication_creation_timestamp`
BEFORE INSERT ON `messaging`.`authentication`
FOR EACH ROW
SET NEW.created_at = NOW()$$

USE `messaging`$$
CREATE
DEFINER=`root`@`%`
TRIGGER `messaging`.`messages_creation_timestamp`
BEFORE INSERT ON `messaging`.`messages`
FOR EACH ROW
SET NEW.created_at = NOW()$$

USE `messaging`$$
CREATE
DEFINER=`root`@`%`
TRIGGER `messaging`.`receiver_creation_timestamp`
BEFORE INSERT ON `messaging`.`receiver`
FOR EACH ROW
SET NEW.created_at = NOW()$$

USE `messaging`$$
CREATE
DEFINER=`root`@`%`
TRIGGER `messaging`.`secret_key_creation_timestamp`
BEFORE INSERT ON `messaging`.`secret_keys`
FOR EACH ROW
SET NEW.created_at = NOW()$$

USE `messaging`$$
CREATE
DEFINER=`root`@`%`
TRIGGER `messaging`.`sender_creation_timestamp`
BEFORE INSERT ON `messaging`.`sender`
FOR EACH ROW
SET NEW.created_at = NOW()$$

USE `messaging`$$
CREATE
DEFINER=`root`@`%`
TRIGGER `messaging`.`user_requests_creation_timestamp`
BEFORE INSERT ON `messaging`.`user_requests`
FOR EACH ROW
SET NEW.created_at = NOW()$$

USE `messaging`$$
CREATE
DEFINER=`root`@`%`
TRIGGER `messaging`.`user_requests_updated_timestamp`
BEFORE UPDATE ON `messaging`.`user_requests`
FOR EACH ROW
SET NEW.updated_at = NOW()$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
