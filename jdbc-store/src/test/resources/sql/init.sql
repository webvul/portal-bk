CREATE TABLE `beehive_user` (
  `beehive_user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(128) NOT NULL,
  `user_password` varchar(45) NOT NULL,
  `activity_token` varchar(45) DEFAULT NULL,
  `mobile` varchar(45) DEFAULT NULL,
  `user_mail` varchar(45) DEFAULT NULL,
  `kii_user_id` varchar(128) DEFAULT NULL,
  `user_name` varchar(45) NOT NULL,
  `role_name` varchar(45) DEFAULT NULL,
  `display_name` varchar(45) DEFAULT NULL,
  `face_subject_id` int(11) DEFAULT NULL,
  `create_by` varchar(45) DEFAULT NULL,
  `modify_by` varchar(45) DEFAULT NULL,
  `create_date` varchar(45) DEFAULT NULL,
  `modify_date` varchar(45) DEFAULT NULL,
  `is_deleted` tinyint(4) DEFAULT '0',
  `enable` tinyint(4) DEFAULT '1',


  PRIMARY KEY (`beehive_user_id`),
  UNIQUE KEY `mobile_UNIQUE` (`mobile`),
  UNIQUE KEY `user_mail_UNIQUE` (`user_mail`),
  UNIQUE KEY `face_subject_id_UNIQUE` (`face_subject_id`),
  KEY `user_name_UNIQUE` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=574 DEFAULT CHARSET=utf8;


CREATE TABLE `beehive_archive_user` (
  `archive_user_id` int(11) NOT NULL AUTO_INCREMENT,
  `beehive_user_id` int(11) NOT NULL,
  `user_name` varchar(45) NOT NULL,
  `user_id` varchar(45) DEFAULT NULL,
  `mobile` varchar(45) DEFAULT NULL,
  `user_mail` varchar(45) DEFAULT NULL,
  `display_name` varchar(45) DEFAULT NULL,
  `role_name` varchar(45) DEFAULT NULL,
  `face_subject_id` int(11) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`archive_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;



CREATE TABLE `global_thing` (
  `id_global_thing` int(11) NOT NULL AUTO_INCREMENT,
  `vendor_thing_id` varchar(45) NOT NULL,
  `kii_app_id` varchar(45) NOT NULL,
  `thing_type` varchar(45) DEFAULT NULL,
  `full_kii_thing_id` varchar(128) DEFAULT NULL,
  `custom_info` mediumtext,
  `status` mediumtext,
  `create_by` varchar(45) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `modify_by` varchar(45) DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
  `schema_name` varchar(45) DEFAULT NULL,
  `schema_version` varchar(45) DEFAULT NULL,
    `is_deleted` tinyint(4) DEFAULT '0',

  PRIMARY KEY (`id_global_thing`,`vendor_thing_id`),
  UNIQUE KEY `vendor_thing_id_UNIQUE` (`vendor_thing_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1090 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `industry_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `thing_type` varchar(20) NOT NULL,
  `name` varchar(20) NOT NULL,
  `version` varchar(10) NOT NULL,
  `content` mediumtext,
  `create_by` varchar(45) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `modify_by` varchar(45) DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
    `is_deleted` tinyint(4) DEFAULT '0',

  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

CREATE TABLE `rel_thing_location` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `thing_id` int(11) NOT NULL,
  `location` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;


CREATE TABLE `rel_group_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_group_id` int(11) NOT NULL,
  `beehive_user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

CREATE TABLE `rel_tag_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_id` int(11) NOT NULL,
  `user_group_id` int(11) NOT NULL,
  `type` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

CREATE TABLE `rel_tag_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_id` int(11) NOT NULL,
  `beehive_user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

CREATE TABLE `rel_team_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_group_id` int(11) NOT NULL,
  `team_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `rel_team_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `team_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `rel_team_thing` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `team_id` int(11) NOT NULL,
  `thing_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `rel_team_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(45) DEFAULT NULL,
  `team_id` int(11) NOT NULL,
  `vaild` int(11) NOT NULL,
  `beehive_user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_rel_team_user_team_id_idx_new` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `rel_thing_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `thing_id` int(11) NOT NULL,
  `user_group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

CREATE TABLE `rel_thing_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `thing_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `thing_id` (`thing_id`),
  KEY `tag_id` (`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `rel_thing_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `thing_id` int(11) NOT NULL,
  `beehive_user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8;

CREATE TABLE `tag_index` (
  `tag_id` int(11) NOT NULL AUTO_INCREMENT,
  `display_name` varchar(45) NOT NULL,
  `tag_type` varchar(45) NOT NULL,
  `full_tag_name` varchar(100) NOT NULL,
  `description` varchar(450) DEFAULT NULL,
  `create_by` varchar(45) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `modify_by` varchar(45) DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
    `is_deleted` tinyint(4) DEFAULT '0',

  PRIMARY KEY (`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1009 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `team` (
  `team_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `create_by` varchar(45) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `modify_by` varchar(45) DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
    `is_deleted` tinyint(4) DEFAULT '0',

  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_group` (
  `user_group_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(450) DEFAULT NULL,
  `create_by` varchar(45) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `modify_by` varchar(45) DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
    `is_deleted` tinyint(4) DEFAULT '0',

  PRIMARY KEY (`user_group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1006 DEFAULT CHARSET=utf8mb4;


