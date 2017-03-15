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
  `yitu_face_image_id` varchar(50) DEFAULT NULL,
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
  `schema_type` varchar(20) NOT NULL COMMENT 'industrytemplate,device,haystack',
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


CREATE TABLE `thing_geo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `global_thing_id` int(11) DEFAULT NULL,
  `vendor_thing_id` varchar(45) DEFAULT NULL,
  `lng` double(17,14) NOT NULL,
  `lat` double(17,14) NOT NULL,
  `floor` int(3) DEFAULT NULL,
  `building_id` varchar(15) DEFAULT NULL,
  `ali_thing_id` varchar(45) DEFAULT NULL,
  `description` mediumtext DEFAULT NULL,
  `geo_type` int(1) DEFAULT NULL COMMENT 'POI点位类型，0：设备；1：非设备',
  `create_by` varchar(45) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `modify_by` varchar(45) DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
  `is_deleted` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

create or replace  view   view_thing_user_ownership  as
select DISTINCT
th.`id_global_thing` as thing_id ,
u.beehive_user_id as user_id ,
tag.tag_id as tag_id ,
g.user_group_id as group_id ,
gg.user_group_id as tag_group_id,
convert(th.create_by,signed) as th_create,
convert(tag.create_by ,signed) as tag_create ,
convert(g.create_by ,signed) as group_create ,
convert(gg.create_by ,signed) as tag_group_create,
if(u.beehive_user_id  in (
 convert(th.create_by , SIGNED),convert (tag.create_by , SIGNED ),convert(g.create_by , SIGNED),convert(gg.create_by,SIGNED))
  ,true,false)  as is_creater
from global_thing th
 left join rel_thing_user  rtu on rtu.`thing_id` = th.`id_global_thing`
 left join rel_thing_tag rtt on rtt.`thing_id` = th.id_global_thing
 left join rel_thing_group rtg on rtg.`thing_id` = th.id_global_thing
 left join tag_index tag on ( tag.`tag_id` = rtt.tag_id)
 left join user_group g on ( g.user_group_id = rtg.user_group_id )

 left join rel_tag_group  rttg on rttg.tag_id =tag.tag_id
 left join user_group gg on (gg.user_group_id = rttg.user_group_id)

 left join rel_tag_user rttu on rttu.tag_id = tag.tag_id
 left join `rel_group_user` rgu on ( rgu.`user_group_id` =  g.user_group_id or rgu.user_group_id = gg.user_group_id  )
 join beehive_user u on ( u.beehive_user_id
 in ( rtu.beehive_user_id , rttu.beehive_user_id , rgu.beehive_user_id ,
 convert(th.create_by , SIGNED),convert (tag.create_by , SIGNED ),convert(g.create_by , SIGNED),convert(gg.create_by,SIGNED))
  );


  CREATE TABLE `user_notice` (
    `user_notice_id` int(11) NOT NULL AUTO_INCREMENT,
    `beehive_user_id` int(11) NOT NULL,
    `readed_time` datetime DEFAULT NULL,
    `create_time` datetime NOT NULL,
    `data` text,
    `msg_in_text` text,
    `title` varchar(128) NOT NULL,
    `notice_type` varchar(45) NOT NULL,
    `readed` tinyint(4) DEFAULT '0',
    `action_type` varchar(11) DEFAULT NULL,
    `from_where` varchar(45) NOT NULL DEFAULT '',
    `addition_strprop` varchar(1024) DEFAULT '',
    `addition_intprop` varchar(256) ,
    PRIMARY KEY (`user_notice_id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=1072 DEFAULT CHARSET=utf8;


  CREATE TABLE `ex_camera_door` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `door_thing_id` int(11) NOT NULL,
    `face_thing_id` int(11) NOT NULL,
    `camera_id` varchar(100) NOT NULL,
    `create_time` datetime DEFAULT NULL,
    `is_deleted` bit(1) DEFAULT NULL COMMENT '删除状态',
    `create_by` varchar(50) DEFAULT NULL,
    `create_date` datetime DEFAULT NULL,
    `modify_by` varchar(50) DEFAULT NULL,
    `modify_date` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;


CREATE TABLE `ex_sit_lock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `space_code` varchar(100) NOT NULL,
  `lock_global_thing_id` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL COMMENT '删除状态',
  `create_by` varchar(50) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `modify_by` varchar(50) DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;


CREATE TABLE `ex_sitsys_beehive_user_rel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sit_sys_user_id` varchar(128) NOT NULL,
  `beehive_user_id` varchar(128) NOT NULL,
  `is_deleted` bit(1) DEFAULT NULL COMMENT '删除状态',
  `create_by` varchar(50) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `modify_by` varchar(50) DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;


CREATE TABLE `ex_space_book` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_code` varchar(100) NOT NULL COMMENT '业务系统app_code',
  `campus_code` varchar(100) NOT NULL COMMENT '园区code',
  `biz_id` varchar(100) DEFAULT NULL COMMENT '业务系统业务类型',
  `biz_type` varchar(100) DEFAULT NULL COMMENT '业务系统业务类型',
  `user_id` varchar(100) NOT NULL COMMENT '业务系统user_id',
  `author_type` varchar NOT NULL COMMENT '1,工位预订；2，内部员工（清洁阿姨，管理 员。。。）加入，只有人脸识别门禁的权限没有工位',
  `password` varchar(11) NOT NULL COMMENT 'xxxx',
  `space_code` varchar(100) DEFAULT NULL COMMENT '空间code',
  `begin_date` datetime NOT NULL COMMENT '开始日期(2016-12-12 13:00:00)',
  `end_date` datetime NOT NULL COMMENT '结束日期(2016-12-12 14:00:00)',
  `is_added_trigger` bit(1) DEFAULT NULL COMMENT '是否创建rule',
  `is_deleted_trigger` bit(1) DEFAULT NULL COMMENT '是否删除rule',
  `create_trigger_error` bit(1) DEFAULT NULL COMMENT '是否删除rule',
  `is_deleted` bit(1) DEFAULT NULL COMMENT '删除状态',
  `create_by` varchar(50) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `modify_by` varchar(50) DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1634 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;


CREATE TABLE `ex_space_book_trigger_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ex_space_book_id` int(11) NOT NULL COMMENT '预订表ID',
  `trigger_id` varchar(100) NOT NULL COMMENT '空间code',
  `is_added_trigger` bit(1) DEFAULT NULL COMMENT '是否创建rule',
  `is_deleted_trigger` bit(1) DEFAULT NULL COMMENT '是否删除rule',
  `type` varchar(20) DEFAULT NULL COMMENT 'unlock,open_door',
  `is_deleted` bit(1) DEFAULT NULL COMMENT '删除状态',
  `create_by` varchar(50) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `modify_by` varchar(50) DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3951 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

