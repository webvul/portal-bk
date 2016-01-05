CREATE TABLE `global_thing` (
  `id_global_thing` int(11) NOT NULL AUTO_INCREMENT,
  `vendor_thing_id` varchar(45) NOT NULL,
  `kii_app_id` varchar(45) NOT NULL,
  `thing_type` varchar(45) DEFAULT NULL,
  `custom_info` mediumtext,
  `status` mediumtext,
  `create_by` varchar(45) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `modify_by` varchar(45) DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id_global_thing`,`vendor_thing_id`),
  UNIQUE KEY `vendor_thing_id_UNIQUE` (`vendor_thing_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `beehive`.`tag_index` (
  `tag_id` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `display_name` VARCHAR(45) NOT NULL COMMENT '',
  `tag_type` VARCHAR(45) NOT NULL COMMENT '',
  `description` VARCHAR(450) NULL COMMENT '',
  `create_by` VARCHAR(45) NULL COMMENT '',
  `create_date` DATETIME NULL COMMENT '',
  `modify_by` VARCHAR(45) NULL COMMENT '',
  `modify_date` DATETIME NULL COMMENT '',
  PRIMARY KEY (`tag_id`) COMMENT ''
  ) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `beehive`.`rel_thing_tag` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `thing_id` INT NOT NULL COMMENT '',
  `tag_id` INT NOT NULL COMMENT '',
  PRIMARY KEY (id)  COMMENT '',
  FOREIGN KEY (thing_id) REFERENCES global_thing(id_global_thing),
  FOREIGN KEY (tag_id) REFERENCES tag_index(tag_id)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE OR REPLACE VIEW beehive.v_rel_thing_tag AS
SELECT
    r.id AS r_id,
	t.tag_id AS t_tag_id,
	t.tag_type AS t_tag_type,
    t.display_name AS t_display_name,
    t.description AS t_description,
    t.create_by AS t_create_by,
    t.create_date AS t_create_date,
    t.modify_by AS t_modify_by,
    t.modify_date AS t_modify_date,
    g.id_global_thing AS g_id_global_thing,
    g.vendor_thing_id AS g_vendor_thing_id,
    g.kii_app_id AS g_kii_app_id,
    g.thing_type AS g_thing_type,
	g.custom_info AS g_custom_info,
    g.status AS g_status,
    g.create_by as g_create_by,
    g.create_date as g_create_date,
    g.modify_by as g_modify_by,
    g.modify_date as g_modify_date
FROM beehive.rel_thing_tag r, beehive.tag_index t, beehive.global_thing g
WHERE r.thing_id=g.id_global_thing AND r.tag_id=t.tag_id


CREATE TABLE beehive.auth_info (
  id INT NOT NULL AUTO_INCREMENT COMMENT '',
  user_id VARCHAR(45) NOT NULL COMMENT '',
  token VARCHAR(45) NULL COMMENT '',
  expire_time DATETIME NULL COMMENT '',
  create_by VARCHAR(45) NULL COMMENT '',
  create_date DATETIME NULL COMMENT '',
  modify_by VARCHAR(45) NULL COMMENT '',
  modify_date DATETIME NULL COMMENT '',
  PRIMARY KEY (id) COMMENT ''
  ) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4;
  
CREATE TABLE IF NOT EXISTS `beehive`.`user_group` (
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
-- Table `beehive`.`rel_group_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `beehive`.`rel_group_user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(45) NOT NULL,
  `user_group_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_rel_group_user_user_group1_idx` (`user_group_id` ASC),
  CONSTRAINT `fk_rel_group_user_user_group1`
    FOREIGN KEY (`user_group_id`)
    REFERENCES `beehive`.`user_group` (`user_group_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `beehive`.`source`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `beehive`.`source` (
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
-- Table `beehive`.`permission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `beehive`.`permission` (
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
    REFERENCES `beehive`.`source` (`source_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 1003
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `beehive`.`rel_group_ permission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `beehive`.`rel_group_ permission` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_group_id` INT(11) NOT NULL,
  `permission_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_rel_group_user_user_group1_idx` (`user_group_id` ASC),
  INDEX `fk_rel_group_user_copy1_permission1_idx` (`permission_id` ASC),
  CONSTRAINT `fk_rel_group_user_user_group10`
    FOREIGN KEY (`user_group_id`)
    REFERENCES `beehive`.`user_group` (`user_group_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_rel_group_user_copy1_permission1`
    FOREIGN KEY (`permission_id`)
    REFERENCES `beehive`.`permission` (`permission_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

