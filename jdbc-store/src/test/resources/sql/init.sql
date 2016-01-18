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
  `tag_full_name` VARCHAR(100) NOT NULL COMMENT '';
  `description` VARCHAR(450) NULL COMMENT '',
  `full_tag_name` varchar(128),
  `create_by` VARCHAR(45) NULL COMMENT '',
  `create_date` DATETIME NULL COMMENT '',
  `modify_by` VARCHAR(45) NULL COMMENT '',
  `modify_date` DATETIME NULL COMMENT '',
  
  PRIMARY KEY (`tag_id`) COMMENT ''
  ) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `rel_thing_tag` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `thing_id` INT NOT NULL COMMENT '',
  `tag_id` INT NOT NULL COMMENT '',
  PRIMARY KEY (id)  COMMENT '',
  FOREIGN KEY (thing_id) REFERENCES global_thing(id_global_thing),
  FOREIGN KEY (tag_id) REFERENCES tag_index(tag_id)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


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
AUTO_INCREMENT = 1003
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
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
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
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 1003
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
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_rel_group_user_permission`
    FOREIGN KEY (`permission_id`)
    REFERENCES `permission` (`permission_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

