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
  
CREATE TABLE 'beehive'.'beehive_user' (
  'user_id' INT NOT NULL AUTO_INCREMENT COMMENT '',
  'kii_user_id' VARCHAR(45) NOT NULL COMMENT '',
  'kii_login_name' VARCHAR(45) NOT NULL COMMENT '',
  'user_name' VARCHAR(45) NOT NULL COMMENT '',
  'phone' VARCHAR(20) NULL COMMENT '',
  'mail' VARCHAR(45) NULL COMMENT '',
  'company' VARCHAR(45) NULL COMMENT '',
  'role' VARCHAR(1) NULL COMMENT '',
  'create_by' VARCHAR(45) NULL COMMENT '',
  'create_date' DATETIME NULL COMMENT '',
  'modify_by' VARCHAR(45) NULL COMMENT '',
  'modify_date' DATETIME NULL COMMENT '',
  PRIMARY KEY ('user_id')
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4;


CREATE TABLE 'beehive'.'beehive_user_group' (
  'user_group_id' INT NOT NULL AUTO_INCREMENT COMMENT '',
  'user_group_name' VARCHAR(45) NOT NULL COMMENT '',
  'description' mediumtext,
  'create_by' VARCHAR(45) NULL COMMENT '',
  'create_date' DATETIME NULL COMMENT '',
  'modify_by' VARCHAR(45) NULL COMMENT '',
  'modify_date' DATETIME NULL COMMENT '',
  PRIMARY KEY ('user_group_id')
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4;


CREATE TABLE 'beehive'.'rel_user_group' (
  'id' INT NOT NULL AUTO_INCREMENT COMMENT '',
  'user_id' INT NOT NULL COMMENT '',
  'user_group_id' INT NOT NULL COMMENT '',
  PRIMARY KEY (id)  COMMENT '',
  FOREIGN KEY (user_id) REFERENCES beehive_user(user_id),
  FOREIGN KEY (user_group_id) REFERENCES beehive_user_group(user_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;