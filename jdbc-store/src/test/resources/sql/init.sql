CREATE TABLE `global_thing` (
  `id_global_thing` int(11) NOT NULL AUTO_INCREMENT,
  `vendor_thing_id` varchar(45) NOT NULL,
  `kii_app_id` varchar(45) NOT NULL,
  `thing_type` varchar(45) DEFAULT NULL,
  `full_kii_thing_id` varchar(128),
  `custom_info` mediumtext,
  `status` mediumtext,
  `create_by` varchar(45) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `modify_by` varchar(45) DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id_global_thing`,`vendor_thing_id`),
  UNIQUE KEY `vendor_thing_id_UNIQUE` (`vendor_thing_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `tag_index` (
  `tag_id` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `display_name` VARCHAR(45) NOT NULL COMMENT '',
  `tag_type` VARCHAR(45) NOT NULL COMMENT '',
  `full_tag_name` VARCHAR(100) NOT NULL COMMENT '',
  `description` VARCHAR(450) NULL COMMENT '',
  `create_by` VARCHAR(45) NULL COMMENT '',
  `create_date` DATETIME NULL COMMENT '',
  `modify_by` VARCHAR(45) NULL COMMENT '',
  `modify_date` DATETIME NULL COMMENT '',
  
  PRIMARY KEY (`tag_id`) COMMENT ''
  ) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4;


-- -----------------------------------------------------
-- Table `rel_thing_tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rel_thing_tag` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `thing_id` INT(11) NOT NULL,
  `tag_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `thing_id` (`thing_id` ASC),
  INDEX `tag_id` (`tag_id` ASC),
  CONSTRAINT `rel_thing_tag_ibfk_1`
    FOREIGN KEY (`thing_id`)
    REFERENCES `global_thing` (`id_global_thing`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `rel_thing_tag_ibfk_2`
    FOREIGN KEY (`tag_id`)
    REFERENCES `tag_index` (`tag_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


CREATE TABLE IF NOT EXISTS `user_group` (
  `user_group_id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(450) NULL,
  `create_by` VARCHAR(45) NULL DEFAULT NULL,
  `create_date` DATETIME NULL DEFAULT NULL,
  `modify_by` VARCHAR(45) NULL DEFAULT NULL,
  `modify_date` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`user_group_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 1000
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `rel_group_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rel_group_user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(45) NOT NULL,
  `user_group_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_rel_group_user_user_group1_idx` (`user_group_id` ASC),
  CONSTRAINT `fk_rel_group_user_user_group1`
    FOREIGN KEY (`user_group_id`)
    REFERENCES `user_group` (`user_group_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `source`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `source` (
  `source_id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `type` VARCHAR(45) NOT NULL,
  `create_by` VARCHAR(45) NULL DEFAULT NULL,
  `create_date` DATETIME NULL DEFAULT NULL,
  `modify_by` VARCHAR(45) NULL DEFAULT NULL,
  `modify_date` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`source_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 1003
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `permission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `permission` (
  `permission_id` INT(11) NOT NULL AUTO_INCREMENT,
  `source_id` INT(11) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `action` VARCHAR(45) NOT NULL,
  `description` VARCHAR(450) NULL,
  `create_by` VARCHAR(45) NULL DEFAULT NULL,
  `create_date` DATETIME NULL DEFAULT NULL,
  `modify_by` VARCHAR(45) NULL DEFAULT NULL,
  `modify_date` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`permission_id`),
  INDEX `fk_permission_source1_idx` (`source_id` ASC),
  CONSTRAINT `fk_permission_source1`
    FOREIGN KEY (`source_id`)
    REFERENCES `source` (`source_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 1000
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `rel_group_permission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rel_group_permission` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_group_id` INT(11) NOT NULL,
  `permission_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_rel_group_user_user_group1_idx` (`user_group_id` ASC),
  INDEX `fk_rel_group_user_permission1_idx` (`permission_id` ASC),
  CONSTRAINT `fk_rel_group_user_user_group10`
    FOREIGN KEY (`user_group_id`)
    REFERENCES `user_group` (`user_group_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_rel_group_user_permission`
    FOREIGN KEY (`permission_id`)
    REFERENCES `permission` (`permission_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `team`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `team` (
  `team_id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `create_by` VARCHAR(45) NULL DEFAULT NULL,
  `create_date` DATETIME NULL DEFAULT NULL,
  `modify_by` VARCHAR(45) NULL DEFAULT NULL,
  `modify_date` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`team_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `rel_team_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rel_team_user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(45) NULL,
  `team_id` INT(11) NOT NULL,
  `vaild` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_rel_team_user_team_id_idx` (`team_id` ASC),
  CONSTRAINT `fk_rel_team_user_team_id`
    FOREIGN KEY (`team_id`)
    REFERENCES `team` (`team_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rel_tag_group`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rel_tag_group` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `tag_id` INT(11) NOT NULL,
  `user_group_id` INT(11) NOT NULL,
  `type` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_rel_tag_group_tag_id_idx` (`tag_id` ASC),
  INDEX `fk_rel_tag_group_user_group_id_idx` (`user_group_id` ASC),
  CONSTRAINT `fk_rel_tag_group_tag_id`
    FOREIGN KEY (`tag_id`)
    REFERENCES `tag_index` (`tag_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_rel_tag_group_user_group_id`
    FOREIGN KEY (`user_group_id`)
    REFERENCES `user_group` (`user_group_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rel_team_group`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rel_team_group` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_group_id` INT(11) NOT NULL,
  `team_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_rel_team_group_user_group_idx` (`user_group_id` ASC),
  INDEX `fk_rel_team_group_team_id_idx` (`team_id` ASC),
  CONSTRAINT `fk_rel_team_group_user_group`
    FOREIGN KEY (`user_group_id`)
    REFERENCES `user_group` (`user_group_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_rel_team_group_team_id`
    FOREIGN KEY (`team_id`)
    REFERENCES `team` (`team_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rel_team_thing`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rel_team_thing` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `team_id` INT(11) NOT NULL,
  `thing_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_rel_team_thing_team_id_idx` (`team_id` ASC),
  INDEX `fk_rel_team_thing_thing_id_idx` (`thing_id` ASC),
  CONSTRAINT `fk_rel_team_thing_team_id`
    FOREIGN KEY (`team_id`)
    REFERENCES `team` (`team_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_rel_team_thing_thing_id`
    FOREIGN KEY (`thing_id`)
    REFERENCES `global_thing` (`id_global_thing`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `rel_team_tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rel_team_tag` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `team_id` INT(11) NOT NULL,
  `tag_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_rel_team_tag_team_id_idx` (`team_id` ASC),
  INDEX `fk_rel_team_tag_tag_id_idx` (`tag_id` ASC),
  CONSTRAINT `fk_rel_team_tag_team_id`
    FOREIGN KEY (`team_id`)
    REFERENCES `team` (`team_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_rel_team_tag_thing_id`
    FOREIGN KEY (`tag_id`)
    REFERENCES `tag_index` (`tag_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;
