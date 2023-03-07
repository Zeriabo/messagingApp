-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';


-- -----------------------------------------------------
-- Table `messaging`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `messaging`.`users` (
  `idusers` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idusers`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `messaging`.`messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `messaging`.`messages` (
  `idmessages` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NOT NULL,
  `messagebody` BLOB NOT NULL,
  `datetime` VARCHAR(45) NOT NULL,
  `nbrofrecipients` INT NOT NULL,
  `idsender` INT NOT NULL,
  PRIMARY KEY (`idmessages`, `idsender`),
  INDEX `fk_messages_users1_idx` (`idsender` ASC) VISIBLE,
  CONSTRAINT `fk_messages_users1`
    FOREIGN KEY (`idsender`)
    REFERENCES `messaging`.`users` (`idusers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `messaging`.`sender`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `messaging`.`sender` (
  `users_idusers` INT NOT NULL,
  `datetime` DATETIME NOT NULL,
  INDEX `fk_sender_users_idx` (`users_idusers` ASC) VISIBLE,
  CONSTRAINT `fk_sender_users`
    FOREIGN KEY (`users_idusers`)
    REFERENCES `messaging`.`users` (`idusers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `messaging`.`receiver`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `messaging`.`receiver` (
  `users_idusers` INT NOT NULL,
  `datetime` DATETIME NOT NULL,
  `messages_idmessages` INT NOT NULL,
  `messages_idsender` INT NOT NULL,
  INDEX `fk_receiver_users1_idx` (`users_idusers` ASC) VISIBLE,
  PRIMARY KEY (`messages_idmessages`, `messages_idsender`),
  CONSTRAINT `fk_receiver_users1`
    FOREIGN KEY (`users_idusers`)
    REFERENCES `messaging`.`users` (`idusers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_receiver_messages1`
    FOREIGN KEY (`messages_idmessages` , `messages_idsender`)
    REFERENCES `messaging`.`messages` (`idmessages` , `idsender`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `messaging`.`secret_keys`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `messaging`.`secret_keys` (
  `idsecret_keys` INT NOT NULL AUTO_INCREMENT,
  `secret_key` BLOB NULL,
  `messages_idmessages` INT NOT NULL,
  `messages_idsender` INT NOT NULL,
  PRIMARY KEY (`idsecret_keys`),
  INDEX `fk_secret_keys_messages1_idx` (`messages_idmessages` ASC, `messages_idsender` ASC) VISIBLE,
  CONSTRAINT `fk_secret_keys_messages1`
    FOREIGN KEY (`messages_idmessages` , `messages_idsender`)
    REFERENCES `messaging`.`messages` (`idmessages` , `idsender`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

INSERT INTO `messaging`.`users`(name,email) 
VALUES
('Zeriab','zeriab@hotmail.com'),
('Zeriab2','zeriab2@hotmail.com'),
('Zeriab3','zeriab3@hotmail.com'),
('Zeriab4','zeriab4@hotmail.com')
;




