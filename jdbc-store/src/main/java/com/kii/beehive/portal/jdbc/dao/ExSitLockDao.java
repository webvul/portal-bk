package com.kii.beehive.portal.jdbc.dao;

import org.springframework.stereotype.Repository;
import com.kii.beehive.portal.jdbc.entity.ExSitLock;

@Repository
public class ExSitLockDao extends SpringBaseDao<ExSitLock> {

	
	public static final String TABLE_NAME = "ex_sit_lock";
	public static final String KEY = "id";





	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}
}
