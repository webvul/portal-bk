package com.kii.beehive.portal.jdbc.dao;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.BeehiveArchiveUser;

@Repository
public class BeehiveArchiveUserDao extends SpringSimpleBaseDao<BeehiveArchiveUser>{


	@Override
	protected String getTableName() {
		return "beehive_archive_user";
	}

	@Override
	protected String getKey() {
		return "archive_user_id";
	}




}
