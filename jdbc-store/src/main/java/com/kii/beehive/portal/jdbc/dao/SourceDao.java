package com.kii.beehive.portal.jdbc.dao;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.Source;

@Repository
public class SourceDao extends SpringBaseDao<Source> {

	
	public static final String TABLE_NAME = "source";
	public static final String KEY = "source_id";
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}

}
