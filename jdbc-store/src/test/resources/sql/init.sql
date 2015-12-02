CREATE TABLE `global_thing` (
  `id_global_thing` int(11) NOT NULL AUTO_INCREMENT,
  `vendor_thing_id` varchar(45) NOT NULL,
  `kii_app_id` varchar(45) NOT NULL,
  `thing_type` varchar(45) DEFAULT NULL,
  `custom_info` mediumblob,
  `status` mediumblob,
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
  PRIMARY KEY (`id`)
  COMMENT '') ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET_utf8mb4;


CREATE TABLE `beehive`.`rel_thing_tag` (
  `thing_id` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `tag_id` INT NOT NULL COMMENT '',
  PRIMARY KEY (`thing_id`, `tag_id`)  COMMENT '')
  ENGINE=InnDB AUTO_INCREMENT=1000 DEFAULT CHARSET_utf8mb4;