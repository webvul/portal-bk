package com.kii.beehive.portal.jdbc.dao;

import org.springframework.stereotype.Repository;
import com.kii.beehive.portal.jdbc.entity.ExSitSysBeehiveUserRel;

@Repository
public class ExSitSysBeehiveUserRelDao extends SpringBaseDao<ExSitSysBeehiveUserRel> {

	
	public static final String TABLE_NAME = "ex_sitsys_beehive_user_rel";
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
